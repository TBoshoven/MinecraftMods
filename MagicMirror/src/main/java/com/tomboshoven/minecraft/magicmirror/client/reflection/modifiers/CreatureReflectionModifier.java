package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.CreatureMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers.CreatureReflectionRendererModifier;
import net.minecraft.world.entity.Entity;

/**
 * A reflection modifier that changes the armor that the entity used to represent the character.
 */
public class CreatureReflectionModifier extends ReflectionModifier {
    @Override
    public <E extends Entity> ReflectionRendererBase<E> apply(MagicMirrorBlockEntityModifier modifier, ReflectionRendererBase<E> reflectionRenderer, Reflection.RenderContext context) {
        if (modifier instanceof CreatureMagicMirrorBlockEntityModifier creatureModifier) {
            return new CreatureReflectionRendererModifier<>(reflectionRenderer, creatureModifier.getEntityType());
        }
        return reflectionRenderer;
    }
}
