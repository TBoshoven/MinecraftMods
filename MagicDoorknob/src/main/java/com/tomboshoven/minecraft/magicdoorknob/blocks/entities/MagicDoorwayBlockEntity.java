package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import java.util.Objects;

/**
 * Tile entity for the magic doorway parts.
 */
public class MagicDoorwayBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    public MagicDoorwayBlockEntity() {
        super(Objects.requireNonNull(BlockEntities.MAGIC_DOORWAY.get()));
    }
}
