package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

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
 * The tile entity for the top mirror block; this tile entity has no reflection logic and simply uses whatever is in the
 * bottom block.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorPartTileEntity extends BlockEntity {
    public MagicMirrorPartTileEntity(BlockPos pos, BlockState state) {
        super(TileEntities.MAGIC_MIRROR_PART.get(), pos, state);
    }

    /**
     * @return The tile entity corresponding to the core of the mirror.
     */
    @Nullable
    public MagicMirrorCoreTileEntity getCore() {
        Block block = getBlockState().getBlock();
        Level level = getLevel();
        if (level != null && block instanceof MagicMirrorActiveBlock) {
            return ((MagicMirrorActiveBlock) block).getCoreBlockEntity(getLevel(), getBlockPos());
        }
        return null;
    }
}
