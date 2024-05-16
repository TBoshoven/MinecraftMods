package com.tomboshoven.minecraft.magicmirror.events;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreTileEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * An event indicating that the set of modifiers of a mirror was updated.
 */
public class MagicMirrorModifiersUpdatedEvent extends Event {
    private final MagicMirrorCoreTileEntity blockEntity;

    /**
     * @param blockEntity The block entity of the updated mirror.
     */
    public MagicMirrorModifiersUpdatedEvent(MagicMirrorCoreTileEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    /**
     * @return the block entity of the updated mirror.
     */
    public MagicMirrorCoreTileEntity getBlockEntity() {
        return blockEntity;
    }
}
