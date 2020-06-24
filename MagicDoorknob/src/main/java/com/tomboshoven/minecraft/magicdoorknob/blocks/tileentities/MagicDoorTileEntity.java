package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * Tile entity for the magic door parts.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicDoorTileEntity extends MagicDoorwayPartBaseTileEntity {
    public MagicDoorTileEntity() {
        super(Objects.requireNonNull(TileEntities.MAGIC_DOOR.get()));
    }
}
