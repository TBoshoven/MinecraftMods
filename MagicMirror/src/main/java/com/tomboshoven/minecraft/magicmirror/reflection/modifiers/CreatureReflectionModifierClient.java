package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.CreatureReflectionRendererModifier;
import net.minecraft.world.entity.EntityType;

/**
 * A reflection modifier that changes the armor that the entity used to represent the character.
 */
public class CreatureReflectionModifierClient extends CreatureReflectionModifier {
    /**
     * @param entityType The type of entity to show in the reflection.
     */
    public CreatureReflectionModifierClient(EntityType<?> entityType) {
        super(entityType);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        return new CreatureReflectionRendererModifier(reflectionRenderer, getEntityType());
    }
}
