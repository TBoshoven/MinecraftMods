package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * The tile entity for the top mirror block; this tile entity has no reflection logic and simply uses whatever's in the
 * bottom block.
 */
public class TileEntityMagicMirrorPart extends TileEntityMagicMirrorBase {
    @Nullable
    @Override
    protected TileEntityMagicMirrorCore getCore() {
        if (!isComplete()) {
            return null;
        }
        TileEntity teBelow = getWorld().getTileEntity(getPos().down());
        if (teBelow instanceof TileEntityMagicMirrorCore) {
            return (TileEntityMagicMirrorCore) teBelow;
        }
        return null;
    }
}
