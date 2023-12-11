package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the magic doorway parts.
 */
public class MagicDoorwayBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    public MagicDoorwayBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_DOORWAY.get(), pos, state);
    }
}
