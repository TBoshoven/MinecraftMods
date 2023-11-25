package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes the color of the rendered model.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DyeReflectionModifier extends ReflectionModifier {
    /**
     * The color for the reflection.
     */
    protected final float[] color;

    /**
     * @param color the color (RGBA) for the reflected entity.
     */
    public DyeReflectionModifier(float[] color) {
        this.color = color;
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // Nothing to do on the server side
        return reflectionRenderer;
    }
}
