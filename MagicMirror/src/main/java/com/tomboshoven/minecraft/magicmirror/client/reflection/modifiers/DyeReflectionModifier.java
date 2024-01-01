package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.DyeMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers.DyeReflectionRendererModifier;

import java.util.stream.IntStream;

/**
 * A reflection modifier that changes the color of the rendered model.
 */
public class DyeReflectionModifier extends ReflectionModifier {
    @Override
    public ReflectionRendererBase apply(MagicMirrorBlockEntityModifier modifier, ReflectionRendererBase reflectionRenderer) {
        if (modifier instanceof DyeMagicMirrorBlockEntityModifier dyeModifier) {
            float[] color = dyeModifier.getColor();
            // Since this is essentially a color filter, we tend to get very dim colors.
            // We can simply make them brighter for a nicer effect.
            IntStream.range(0, 3).forEach(i -> color[i] *= 2);
            return new DyeReflectionRendererModifier(reflectionRenderer, color);
        }
        return reflectionRenderer;
    }
}
