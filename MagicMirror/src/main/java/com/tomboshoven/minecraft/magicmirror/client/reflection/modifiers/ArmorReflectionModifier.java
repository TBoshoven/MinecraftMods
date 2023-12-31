package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.ArmorMagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers.ArmorReflectionRendererModifier;

/**
 * A reflection modifier that changes the armor that the reflected character is wearing.
 */
public final class ArmorReflectionModifier extends ReflectionModifier {
    @Override
    public ReflectionRendererBase apply(MagicMirrorTileEntityModifier modifier, ReflectionRendererBase reflectionRenderer) {
        if (modifier instanceof ArmorMagicMirrorTileEntityModifier) {
            ArmorMagicMirrorTileEntityModifier armorModifier = (ArmorMagicMirrorTileEntityModifier) modifier;
            return new ArmorReflectionRendererModifier(reflectionRenderer, armorModifier.getReplacementArmor());
        }
        return reflectionRenderer;
    }
}
