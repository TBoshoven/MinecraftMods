package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorActiveBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The block entity for the top mirror block.
 * This block entity has no reflection logic and simply uses whatever is in the bottom block.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorPartBlockEntity extends BlockEntity {
    public MagicMirrorPartBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_MIRROR_PART.get(), pos, state);
    }

    /**
     * @return The block entity corresponding to the core of the mirror.
     */
    @Nullable
    public MagicMirrorCoreBlockEntity getCore() {
        Block block = getBlockState().getBlock();
        Level level = getLevel();
        if (level != null && block instanceof MagicMirrorActiveBlock) {
            return ((MagicMirrorActiveBlock) block).getCoreBlockEntity(getLevel(), getBlockPos());
        }
        return null;
    }
}
