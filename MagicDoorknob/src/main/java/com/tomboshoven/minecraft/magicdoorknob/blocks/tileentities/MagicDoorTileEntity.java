package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Tile entity for the magic door parts.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicDoorTileEntity extends MagicDoorwayPartBaseTileEntity {
    public MagicDoorTileEntity() {
        super(TileEntities.MAGIC_DOOR.get());
    }
}
