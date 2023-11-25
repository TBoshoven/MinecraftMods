package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A modifier for a reflection renderer which colorizes the reflected entity.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DyeReflectionRendererModifier extends ReflectionRendererModifier {
    /**
     * The color for the reflection.
     */
    protected final float[] color;

    /**
     * @param baseRenderer The renderer that is being proxied.
     * @param color        The color (RGBA) for the reflected entity.
     */
    public DyeReflectionRendererModifier(ReflectionRendererBase baseRenderer, float[] color) {
        super(baseRenderer);
        this.color = color;
    }

    @Override
    public void render(float facing, float partialTicks, MultiBufferSource.BufferSource renderTypeBuffer, @Nullable float[] colorize) {
        super.render(facing, partialTicks, renderTypeBuffer, color);
    }
}
