package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.CreatureMagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers.CreatureReflectionRendererModifier;

/**
 * A reflection modifier that changes the armor that the entity used to represent the character.
 */
public class CreatureReflectionModifier extends ReflectionModifier {
    @Override
    public ReflectionRendererBase apply(MagicMirrorTileEntityModifier modifier, ReflectionRendererBase reflectionRenderer) {
        if (modifier instanceof CreatureMagicMirrorTileEntityModifier) {
            return new CreatureReflectionRendererModifier(reflectionRenderer);
        }
        return reflectionRenderer;
    }
}
