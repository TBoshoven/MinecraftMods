package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers.ArmorReflectionRendererModifier;
import net.minecraft.world.entity.Entity;

/**
 * A reflection modifier that changes the armor that the reflected character is wearing.
 */
public final class ArmorReflectionModifier extends ReflectionModifier {
    @Override
    public <E extends Entity> ReflectionRendererBase<E> apply(MagicMirrorBlockEntityModifier modifier, ReflectionRendererBase<E> reflectionRenderer, Reflection.RenderContext context) {
        if (modifier instanceof ArmorMagicMirrorBlockEntityModifier armorModifier) {
            return new ArmorReflectionRendererModifier<>(reflectionRenderer, armorModifier.getReplacementArmor(), context);
        }
        return reflectionRenderer;
    }
}
