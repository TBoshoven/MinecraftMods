package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorInactiveBlock.EnumPartType.BOTTOM;

public class MagicMirrorPartBlock extends MagicMirrorActiveBlock {
    /**
     * Create a new Magic Mirror block.
     */
    MagicMirrorPartBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockPos getCoreBlockPos(BlockPos pos) {
        return pos.below();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MagicMirrorPartBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        // Change the other part to incomplete
        BlockPos otherPos = pos.below();
        BlockState otherState = worldIn.getBlockState(otherPos);
        if (otherState.getBlock() == Blocks.MAGIC_MIRROR_CORE.get()) {
            worldIn.setBlockAndUpdate(
                    otherPos,
                    Blocks.MAGIC_MIRROR_INACTIVE.get().defaultBlockState()
                            .setValue(MagicMirrorInactiveBlock.PART, BOTTOM)
                            .setValue(HorizontalDirectionalBlock.FACING, state.getValue(HorizontalDirectionalBlock.FACING))
            );
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }
}
