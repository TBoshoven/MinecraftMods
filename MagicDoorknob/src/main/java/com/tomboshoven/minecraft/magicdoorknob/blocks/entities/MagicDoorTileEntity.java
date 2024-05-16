package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import java.util.Objects;

/**
 * Tile entity for the magic door parts.
 */
public class MagicDoorTileEntity extends MagicDoorwayPartBaseTileEntity {
    public MagicDoorTileEntity() {
        super(Objects.requireNonNull(TileEntities.MAGIC_DOOR.get()));
    }
}
