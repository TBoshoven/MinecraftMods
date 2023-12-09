package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.world.entity.EntityType;

/**
 * A reflection modifier that changes which entity model is rendered.
 */
public class CreatureReflectionModifier extends ReflectionModifier {
    /**
     * The type of entity to show in the reflection.
     */
    private final EntityType<?> entityType;

    /**
     * @param entityType The type of entity to show in the reflection.
     */
    public CreatureReflectionModifier(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // Nothing to do on the server side
        return reflectionRenderer;
    }

    /**
     * @return The type of entity to show in the reflection.
     */
    protected EntityType<?> getEntityType() {
        return entityType;
    }
}
