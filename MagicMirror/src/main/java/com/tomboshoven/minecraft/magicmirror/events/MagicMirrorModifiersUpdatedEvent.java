package com.tomboshoven.minecraft.magicmirror.events;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event indicating that the set of modifiers of a mirror was updated.
 */
public class MagicMirrorModifiersUpdatedEvent extends Event {
    private final MagicMirrorCoreBlockEntity blockEntity;

    /**
     * @param blockEntity The block entity of the updated mirror.
     */
    public MagicMirrorModifiersUpdatedEvent(MagicMirrorCoreBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    /**
     * @return the block entity of the updated mirror.
     */
    public MagicMirrorCoreBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
