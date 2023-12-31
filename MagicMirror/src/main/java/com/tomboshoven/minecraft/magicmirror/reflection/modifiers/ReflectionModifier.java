package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;

/**
 * A modification of the reflection, used for changing what the reflection looks like when combined with the
 * ReflectionRendererModifier.
 */
public abstract class ReflectionModifier {
    /**
     * Apply the modification to the reflection renderer.
     *
     * @param modifier The block entity modifier to use for the reflection.
     * @param reflectionRenderer The renderer to be changed.
     * @return The updated renderer.
     */
    public abstract ReflectionRendererBase apply(MagicMirrorTileEntityModifier modifier, ReflectionRendererBase reflectionRenderer);
}
