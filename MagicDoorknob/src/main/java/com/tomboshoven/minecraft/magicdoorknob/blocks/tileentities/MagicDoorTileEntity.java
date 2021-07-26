package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Tile entity for the magic door parts.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicDoorTileEntity extends MagicDoorwayPartBaseTileEntity {
    public MagicDoorTileEntity(BlockPos pos, BlockState state) {
        super(TileEntities.MAGIC_DOOR.get(), pos, state);
    }
}
