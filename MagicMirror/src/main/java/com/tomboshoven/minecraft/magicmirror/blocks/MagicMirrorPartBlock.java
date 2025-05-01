package com.tomboshoven.minecraft.magicmirror.blocks;

import com.mojang.serialization.MapCodec;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorInactiveBlock.EnumPartType.BOTTOM;

public class MagicMirrorPartBlock extends MagicMirrorActiveBlock {
    private static final MapCodec<MagicMirrorCoreBlock> CODEC = simpleCodec(MagicMirrorCoreBlock::new);

    /**
     * Create a new Magic Mirror block.
     */
    MagicMirrorPartBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
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

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        level.updateNeighborsAt(pos.below(), this);
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (neighborPos.equals(pos.below()) && !neighborState.is(Blocks.MAGIC_MIRROR_CORE.get())) {
            return Blocks.MAGIC_MIRROR_INACTIVE.get().defaultBlockState()
                    .setValue(MagicMirrorInactiveBlock.PART, BOTTOM)
                    .setValue(FACING, state.getValue(FACING));
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }
}
