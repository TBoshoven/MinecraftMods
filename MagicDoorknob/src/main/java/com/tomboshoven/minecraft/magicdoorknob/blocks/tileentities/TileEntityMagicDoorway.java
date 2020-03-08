package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Tile entity for the magic doorway parts.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityMagicDoorway extends TileEntityMagicDoorwayPartBase {
    public TileEntityMagicDoorway() {
        super(TileEntities.MAGIC_DOORWAY);
    }
}
