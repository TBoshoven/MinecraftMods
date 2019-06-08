package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A modification of the reflection, used for changing what the reflection looks like when combined with the
 * ReflectionRendererModifier.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ReflectionModifier {
    /**
     * Apply the modification to the reflection renderer.
     *
     * @param reflectionRenderer The renderer to be changed.
     * @return The updated renderer.
     */
    public abstract ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer);
}
