package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Tile entity for the magic door parts.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityMagicDoor extends TileEntityMagicDoorwayPartBase {
    public TileEntityMagicDoor() {
        super(TileEntities.MAGIC_DOOR);
    }
}
