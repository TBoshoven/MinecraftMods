package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Tile entity for the magic doorway parts.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicDoorwayTileEntity extends MagicDoorwayPartBaseTileEntity {
    public MagicDoorwayTileEntity() {
        super(TileEntities.MAGIC_DOORWAY.get());
    }
}
