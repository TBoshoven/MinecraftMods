package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * Part of a magic doorway.
 */
public class MagicDoorwayBlock extends MagicDoorwayPartBaseBlock {
    /**
     * Property describing which part of the doorway is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);

    // Awkward representation of blockstate for backward compatibility
    // TODO: Fix this upon major update

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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, OPEN_NORTH_SOUTH, OPEN_EAST_WEST, OPEN_CROSS_TOP_BOTTOM);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MagicDoorwayBlockEntity(pos, state);
    }

    /**
     * Try closing the doorway.
     * Closes immediately if configured without suffocation prevention, or if there are no living entities in it.
     *
     * @param level             The level containing the doorway.
     * @param pos               The position of the doorway block.
     * @param updateBlockEntity Whether to update the associated block entity if needed.
     */
    private void tryClose(Level level, BlockPos pos, boolean updateBlockEntity) {
        if (Config.SERVER.preventSuffocation.getAsBoolean() && level.hasEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(pos), e -> true)) {
            if (updateBlockEntity && level.getBlockEntity(pos) instanceof MagicDoorwayBlockEntity blockEntity) {
                blockEntity.setClosing();
            }
            level.scheduleTick(pos, asBlock(), 10);
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
    public void tryClose(Level level, BlockPos pos) {
        tryClose(level, pos, true);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        // Try closing the door again
        if (level.getBlockEntity(pos) instanceof MagicDoorwayBlockEntity blockEntity) {
            if (blockEntity.isClosing()) {
                tryClose(level, pos, false);
            }
        }
    }
}
