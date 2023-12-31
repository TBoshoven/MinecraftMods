package com.tomboshoven.minecraft.magicmirror.events;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * An event indicating that the reflected entity in a mirror is changed.
 */
public class MagicMirrorReflectedEntityEvent extends Event {
    private final MagicMirrorCoreBlockEntity blockEntity;
    private final Entity reflectedEntity;

    /**
     * @param blockEntity The block entity of the updated mirror.
     * @param reflectedEntity The new reflected entity. May be null, indicating no reflected entity.
     */
    public MagicMirrorReflectedEntityEvent(MagicMirrorCoreBlockEntity blockEntity, @Nullable Entity reflectedEntity) {
        this.blockEntity = blockEntity;
        this.reflectedEntity = reflectedEntity;
    }

    /**
     * @return the block entity of the updated mirror.
     */
    public MagicMirrorCoreBlockEntity getBlockEntity() {
        return blockEntity;
    }

    /**
     * @return the new reflected entity. May be null, indicating there is no currently reflected entity.
     */
    @Nullable
    public Entity getReflectedEntity() {
        return reflectedEntity;
    }
}
