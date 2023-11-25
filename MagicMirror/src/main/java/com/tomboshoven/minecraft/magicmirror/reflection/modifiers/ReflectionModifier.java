package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
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
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        return reflectionRenderer;
    }

    /**
     * Apply the modification to the name tag.
     *
     * @param nameTag The name tag to be changed.
     * @return The updated name tag.
     */
    @Nullable
    public Component applyNameTag(@Nullable Component nameTag) {
        return nameTag;
    }
}
