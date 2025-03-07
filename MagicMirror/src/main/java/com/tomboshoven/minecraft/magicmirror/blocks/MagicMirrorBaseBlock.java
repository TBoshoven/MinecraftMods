package com.tomboshoven.minecraft.magicmirror.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

abstract class MagicMirrorBaseBlock extends HorizontalDirectionalBlock {
    /**
     * The bounding boxes of the various orientations of this block; should be indexed by facing.horizontalIndex()
     */
    private static final VoxelShape[] SHAPES = {
            // South
            box(0, 0, 0, 16, 16, 2),
            // West
            box(14, 0, 0, 16, 16, 16),
            // North
            box(0, 0, 14, 16, 16, 16),
            // East
            box(0, 0, 0, 2, 16, 16),
    };

    /**
     * Create a new Magic Mirror block.
     */
    MagicMirrorBaseBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Make sure the mirror is facing the right way when placed
        Direction horizontalDirection = Direction.NORTH;
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                horizontalDirection = direction;
                break;
            }
        }
        return defaultBlockState().setValue(FACING, horizontalDirection.getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
