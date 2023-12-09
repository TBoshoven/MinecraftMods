package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the magic door parts.
 */
public class MagicDoorBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    public MagicDoorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_DOOR.get(), pos, state);
    }
}
