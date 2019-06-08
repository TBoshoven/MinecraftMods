package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Modifier for how the reflection is rendered.
 * Contains some basic functionality for overriding part of the rendering behavior.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class ReflectionRendererModifier extends ReflectionRendererBase {
    /**
     * The renderer that is being proxied.
     */
    private final ReflectionRendererBase baseRenderer;

    /**
     * @param baseRenderer The renderer that is being proxied.
     */
    ReflectionRendererModifier(ReflectionRendererBase baseRenderer) {
        this.baseRenderer = baseRenderer;
    }

    @Override
    public Entity getEntity() {
        return baseRenderer.getEntity();
    }

    @Override
    public Render<? extends Entity> getRenderer() {
        return baseRenderer.getRenderer();
    }

    @Override
    public void setRenderer(Render<? extends Entity> renderer) {
        baseRenderer.setRenderer(renderer);
    }

    @Override
    public void render(float facing, float partialTicks) {
        baseRenderer.render(facing, partialTicks);
    }
}
