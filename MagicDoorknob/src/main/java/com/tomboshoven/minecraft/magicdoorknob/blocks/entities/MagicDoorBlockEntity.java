package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import java.util.Objects;

/**
 * Tile entity for the magic door parts.
 */
public class MagicDoorBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    public MagicDoorBlockEntity() {
        super(Objects.requireNonNull(BlockEntities.MAGIC_DOOR.get()));
    }
}
