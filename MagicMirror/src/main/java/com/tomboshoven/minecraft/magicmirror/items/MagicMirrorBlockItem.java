package com.tomboshoven.minecraft.magicmirror.items;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Block item for the Magic Mirror.
 * Handles custom placement logic related to activating mirrors.
 */
public class MagicMirrorBlockItem extends BlockItem {
    public MagicMirrorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockPos targetPos = context.getClickedPos();
        Level level = context.getLevel();
        Block block = Blocks.MAGIC_MIRROR_INACTIVE.get();
        BlockState targetState = block.getStateForPlacement(context);
        if (targetState == null) {
            return null;
        }
        Direction targetFacing = targetState.getValue(HorizontalDirectionalBlock.FACING);

        BlockState blockAbove = level.getBlockState(targetPos.above());
        BlockState blockBelow = level.getBlockState(targetPos.below());
        if (blockBelow.getBlock() == Blocks.MAGIC_MIRROR_INACTIVE.get() && blockBelow.getValue(HorizontalDirectionalBlock.FACING) == targetFacing) {
            block = Blocks.MAGIC_MIRROR_PART.get();
        } else if (blockAbove.getBlock() == Blocks.MAGIC_MIRROR_INACTIVE.get() && blockAbove.getValue(HorizontalDirectionalBlock.FACING) == targetFacing) {
            block = Blocks.MAGIC_MIRROR_CORE.get();
        }
        return block.getStateForPlacement(context);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        // If there is an inactive block above or below, we should activate it
        Level level = context.getLevel();
        Block block = state.getBlock();
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        if (block == Blocks.MAGIC_MIRROR_CORE.get()) {
            if (!level.setBlockAndUpdate(context.getClickedPos().above(), Blocks.MAGIC_MIRROR_PART.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing))) {
                return false;
            }
        } else if (block == Blocks.MAGIC_MIRROR_PART.get()) {
            if (!level.setBlockAndUpdate(context.getClickedPos().below(), Blocks.MAGIC_MIRROR_CORE.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing))) {
                return false;
            }
        }

        return super.placeBlock(context, state);
    }
}
