package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.CreatureReflectionRendererModifier;

/**
 * A reflection modifier that changes the armor that the entity used to represent the character.
 */
public class CreatureReflectionModifierClient extends CreatureReflectionModifier {
    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        return new CreatureReflectionRendererModifier(reflectionRenderer);
    }
}
