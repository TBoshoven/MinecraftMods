package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Tile entity for the magic doorway parts.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicDoorwayTileEntity extends MagicDoorwayPartBaseTileEntity {
    public MagicDoorwayTileEntity(BlockPos pos, BlockState state) {
        super(TileEntities.MAGIC_DOORWAY.get(), pos, state);
    }
}
