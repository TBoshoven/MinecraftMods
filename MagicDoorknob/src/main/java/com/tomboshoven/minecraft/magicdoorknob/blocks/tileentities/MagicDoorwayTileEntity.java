package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import java.util.Objects;

/**
 * Tile entity for the magic doorway parts.
 */
public class MagicDoorwayTileEntity extends MagicDoorwayPartBaseTileEntity {
    public MagicDoorwayTileEntity() {
        super(Objects.requireNonNull(TileEntities.MAGIC_DOORWAY.get()));
    }
}
