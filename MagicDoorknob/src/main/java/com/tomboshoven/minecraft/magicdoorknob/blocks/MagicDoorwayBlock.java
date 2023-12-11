package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * Part of a magic doorway.
 */
public class MagicDoorwayBlock extends MagicDoorwayPartBaseBlock {
    /**
     * Property describing which part of the doorway is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);

    /**
     * Property describing whether the block forms a doorway between north and south.
     */
    public static final BooleanProperty OPEN_NORTH_SOUTH = BooleanProperty.create("open_north_south");

    /**
     * Property describing whether the block forms a doorway between east and west.
     */
    public static final BooleanProperty OPEN_EAST_WEST = BooleanProperty.create("open_east_west");

    private static final VoxelShape BOUNDING_BOX_PILLAR_NW = box(0, 0, 15, 1, 16, 16);
    private static final VoxelShape BOUNDING_BOX_PILLAR_NE = box(15, 0, 0, 16, 16, 15);
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
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        boolean openNorthSouth = state.getValue(OPEN_NORTH_SOUTH);
        boolean openEastWest = state.getValue(OPEN_EAST_WEST);
        boolean isTop = state.getValue(PART) == EnumPartType.TOP;
        VoxelShape result = Shapes.empty();
        if (openNorthSouth && openEastWest) {
            result = Shapes.or(result, BOUNDING_BOX_PILLAR_NE, BOUNDING_BOX_PILLAR_NW, BOUNDING_BOX_PILLAR_SE, BOUNDING_BOX_PILLAR_SW);
        } else {
            if (!openNorthSouth) {
                result = Shapes.or(result, BOUNDING_BOX_WALL_N, BOUNDING_BOX_WALL_S);
            }
            if (!openEastWest) {
                result = Shapes.or(result, BOUNDING_BOX_WALL_E, BOUNDING_BOX_WALL_W);
            }
        }
        if (isTop) {
            result = Shapes.or(result, BOUNDING_BOX_TOP);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.isAir()) {
            // When this block is destroyed (manually or by closing the door), replace it by its base block.
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof MagicDoorwayBlockEntity) {
                worldIn.setBlockAndUpdate(pos, ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState());
            }
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, OPEN_NORTH_SOUTH, OPEN_EAST_WEST);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MagicDoorwayBlockEntity(pos, state);
    }
}
