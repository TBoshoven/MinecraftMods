package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

/**
 * Modifier for how the reflection is rendered.
 * Contains some basic functionality for overriding part of the rendering behavior.
 */
class ReflectionRendererModifier<E extends Entity> extends ReflectionRendererBase<E> {
    /**
     * The renderer that is being proxied.
     */
    private final ReflectionRendererBase<? extends E> baseRenderer;

    /**
     * @param baseRenderer The renderer that is being proxied.
     */
    ReflectionRendererModifier(ReflectionRendererBase<? extends E> baseRenderer) {
        this.baseRenderer = baseRenderer;
    }

    @Override
    public final E getEntity() {
        return baseRenderer.getEntity();
    }

    @Override
    public final void replaceRenderer(EntityRenderer<? super E, ?> renderer) {
        baseRenderer.replaceRenderer(renderer);
    }

    @Override
    public void setUp() {
        baseRenderer.setUp();
    }

    @Override
    public void tearDown() {
        baseRenderer.tearDown();
    }

    @Override
    public EntityRenderState updateState(float partialTicks) {
        return baseRenderer.updateState(partialTicks);
    }

    @Override
    public void render(float facing, MultiBufferSource renderTypeBuffer) {
        baseRenderer.render(facing, renderTypeBuffer);
    }

    @Override
    public void close() throws Exception {
        baseRenderer.close();
    }
}
