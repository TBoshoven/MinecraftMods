package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The tile entity for the top mirror block; this tile entity has no reflection logic and simply uses whatever's in the
 * bottom block.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityMagicMirrorPart extends TileEntity {
    // Some values copied from the blockstate
    private EnumFacing facing = EnumFacing.NORTH;
    private BlockMagicMirror.EnumPartType part = BlockMagicMirror.EnumPartType.TOP;
    private boolean complete = false;

    @Override
    public void onLoad() {
        // Synchronize with blockstate; we need to know some of this in order to render the reflection.
        IBlockState blockState = getWorld().getBlockState(getPos());
        Block block = blockState.getBlock();
        if (block instanceof BlockMagicMirror) {
            BlockMagicMirror blockMagicMirror = (BlockMagicMirror) block;
            facing = blockMagicMirror.getFacing(blockState);
            part = blockMagicMirror.getPart(blockState);
            complete = blockMagicMirror.isComplete(blockState);
        }
    }

    /**
     * @return The reflection in the mirror.
     */
    @Nullable
    public Reflection getReflection() {
        TileEntity teBelow = getWorld().getTileEntity(getPos().down());
        if (teBelow instanceof TileEntityMagicMirrorCore) {
            return ((TileEntityMagicMirrorCore) teBelow).getReflection();
        }
        return null;
    }

    /**
     * @return Which direction the mirror is facing in.
     */
    public EnumFacing getFacing() {
        return facing;
    }

    /**
     * @return Which part of the mirror this tile entity belongs to.
     */
    public BlockMagicMirror.EnumPartType getPart() {
        return part;
    }

    /**
     * @return Whether the mirror is completely constructed.
     */
    public boolean isComplete() {
        return complete;
    }
}
