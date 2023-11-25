package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.DyeReflectionRendererModifier;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes the color of the rendered model.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DyeReflectionModifierClient extends DyeReflectionModifier {
    /**
     * @param color the color (RGBA) for the reflected entity.
     */
    public DyeReflectionModifierClient(float[] color) {
        super(color);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // By default, colors will be darker
        // Normally the color modulator ends up being a multiplication, we should be able to make it a bit brighter
        return new DyeReflectionRendererModifier(reflectionRenderer, new float[] { color[0] * 2, color[1] * 2, color[2] * 2, color[3] });
    }
}
