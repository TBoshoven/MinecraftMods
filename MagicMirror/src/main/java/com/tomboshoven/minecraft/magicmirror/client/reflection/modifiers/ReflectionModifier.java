package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.world.entity.Entity;

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
    public abstract <E extends Entity> ReflectionRendererBase<E> apply(MagicMirrorBlockEntityModifier modifier, ReflectionRendererBase<E> reflectionRenderer, Reflection.RenderContext context);
}
