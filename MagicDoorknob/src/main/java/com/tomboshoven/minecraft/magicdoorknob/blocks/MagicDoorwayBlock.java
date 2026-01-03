package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Part of a magic doorway.
 */
public class MagicDoorwayBlock extends MagicDoorwayPartBaseBlock {
    /**
     * Property describing which part of the doorway is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);

    // Awkward representation of blockstate for backward compatibility
    // To be fixed up in the future

    /**
     * Property describing whether the doorway represents both the top and bottom of crossing doorways.
     */
    public static final BooleanProperty OPEN_CROSS_TOP_BOTTOM = BooleanProperty.create("open_cross_top_bottom");

    /**
     * Property describing whether the block forms a doorway between north and south.
     */
    public static final BooleanProperty OPEN_NORTH_SOUTH = BooleanProperty.create("open_north_south");

    /**
     * Property describing whether the block forms a doorway between east and west.
     */
    public static final BooleanProperty OPEN_EAST_WEST = BooleanProperty.create("open_east_west");

    private static final VoxelShape BOUNDING_BOX_PILLAR_NW = box(0, 0, 15, 1, 16, 16);
    private static final VoxelShape BOUNDING_BOX_PILLAR_NE = box(15, 0, 0, 16, 16, 1);
    private static final VoxelShape BOUNDING_BOX_PILLAR_SW = box(0, 0, 0, 1, 16, 1);
    private static final VoxelShape BOUNDING_BOX_PILLAR_SE = box(15, 0, 15, 16, 16, 16);

    private static final VoxelShape BOUNDING_BOX_WALL_S = box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape BOUNDING_BOX_WALL_N = box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape BOUNDING_BOX_WALL_E = box(0, 0, 0, 1, 16, 16);
    private static final VoxelShape BOUNDING_BOX_WALL_W = box(15, 0, 0, 16, 16, 16);

    private static final VoxelShape BOUNDING_BOX_TOP = box(0, 15, 0, 16, 16, 16);

    /**
     * Create a new Magic Doorway block.
     */
    MagicDoorwayBlock(Block.Properties properties) {
        super(properties);

        // By default, the doorway is not open in any direction
        registerDefaultState(
                stateDefinition.any()
                        .setValue(PART, EnumPartType.BOTTOM)
                        .setValue(OPEN_EAST_WEST, Boolean.TRUE)
                        .setValue(OPEN_NORTH_SOUTH, Boolean.FALSE)
                        .setValue(OPEN_CROSS_TOP_BOTTOM, Boolean.FALSE)
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        boolean openNorthSouth = state.getValue(OPEN_NORTH_SOUTH);
        boolean openEastWest = state.getValue(OPEN_EAST_WEST);
        boolean isTop = state.getValue(PART) == EnumPartType.TOP;
        VoxelShape result = VoxelShapes.empty();
        if (openNorthSouth && openEastWest) {
            result = VoxelShapes.or(result, BOUNDING_BOX_PILLAR_NE, BOUNDING_BOX_PILLAR_NW, BOUNDING_BOX_PILLAR_SE, BOUNDING_BOX_PILLAR_SW);
        } else {
            if (!openNorthSouth) {
                result = VoxelShapes.or(result, BOUNDING_BOX_WALL_N, BOUNDING_BOX_WALL_S);
            }
            if (!openEastWest) {
                result = VoxelShapes.or(result, BOUNDING_BOX_WALL_E, BOUNDING_BOX_WALL_W);
            }
        }
        if (isTop) {
            result = VoxelShapes.or(result, BOUNDING_BOX_TOP);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.isAir(worldIn, pos)) {
            // When this block is destroyed (manually or by closing the door), replace it by its base block.
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof MagicDoorwayBlockEntity) {
                worldIn.setBlockAndUpdate(pos, ((MagicDoorwayPartBaseBlockEntity) tileEntity).getBaseBlockState());
            }
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PART, OPEN_NORTH_SOUTH, OPEN_EAST_WEST, OPEN_CROSS_TOP_BOTTOM);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MagicDoorwayBlockEntity();
    }

    /**
     * Try closing the doorway.
     * Closes immediately if configured without suffocation prevention, or if there are no living entities in it.
     *
     * @param level             The level containing the doorway.
     * @param pos               The position of the doorway block.
     * @param updateBlockEntity Whether to update the associated block entity if needed.
     */
    private void tryClose(World level, BlockPos pos, boolean updateBlockEntity) {
        if (Config.SERVER.preventSuffocation.get() && !level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos)).isEmpty()) {
            if (updateBlockEntity) {
                TileEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof MagicDoorwayBlockEntity) {
                    ((MagicDoorwayBlockEntity) blockEntity).setClosing();
                }
            }
            level.getBlockTicks().scheduleTick(pos, asBlock(), 10);
        } else {
            level.destroyBlock(pos, false);
        }
    }


    /**
     * Try closing the doorway.
     * Closes immediately if configured without suffocation prevention, or if there are no living entities in it.
     *
     * @param level The level containing the doorway.
     * @param pos   The position of the doorway block.
     */
    public void tryClose(World level, BlockPos pos) {
        tryClose(level, pos, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        // Try closing the door again
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayBlockEntity) {
            if (((MagicDoorwayBlockEntity) blockEntity).isClosing()) {
                tryClose(level, pos, false);
            }
        }
    }
}
