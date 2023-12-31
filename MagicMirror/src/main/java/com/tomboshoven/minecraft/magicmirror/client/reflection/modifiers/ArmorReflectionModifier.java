package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers.ArmorReflectionRendererModifier;

/**
 * A reflection modifier that changes the armor that the reflected character is wearing.
 */
public final class ArmorReflectionModifier extends ReflectionModifier {
    @Override
    public ReflectionRendererBase apply(MagicMirrorBlockEntityModifier modifier, ReflectionRendererBase reflectionRenderer) {
        if (modifier instanceof ArmorMagicMirrorBlockEntityModifier armorModifier) {
            return new ArmorReflectionRendererModifier(reflectionRenderer, armorModifier.getReplacementArmor());
        }
        return reflectionRenderer;
    }
}
