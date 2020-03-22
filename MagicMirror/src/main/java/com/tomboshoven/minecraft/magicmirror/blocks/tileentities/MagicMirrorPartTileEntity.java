package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The tile entity for the top mirror block; this tile entity has no reflection logic and simply uses whatever's in the
 * bottom block.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorPartTileEntity extends MagicMirrorBaseTileEntity {
    public MagicMirrorPartTileEntity() {
        super(TileEntities.MAGIC_MIRROR_PART);
    }

    @Nullable
    @Override
    protected MagicMirrorCoreTileEntity getCore() {
        if (!isComplete()) {
            return null;
        }
        TileEntity teBelow = getWorld().getTileEntity(getPos().down());
        if (teBelow instanceof MagicMirrorCoreTileEntity) {
            return (MagicMirrorCoreTileEntity) teBelow;
        }
        return null;
    }
}
