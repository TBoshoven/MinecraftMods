package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

/**
 * Base class for reflection renderers.
 */
public abstract class ReflectionRendererBase<E extends Entity> {
    /**
     * @return The entity that is being rendered. This is used for chaining modifiers.
     */
    public abstract E getEntity();

    /**
     * Replace the renderer with another one (for a compatible entity type).
     *
     * @param renderer The new renderer to use.
     */
    public abstract void replaceRenderer(EntityRenderer<? super E, ?> renderer);

    /**
     * Set up the rendering perspective.
     * Always call tearDownPerspective after rendering.
     */
    public abstract void setUp();

    /**
     * Tear down the rendering perspective.
     * Always call setupPerspective before this.
     */
    public abstract void tearDown();

    /**
     * Render the reflection.
     *
     * @param facing       The rotation (in degrees) for the entity that is being rendered.
     * @param partialTicks The partial ticks, for smooth rendering.
     */
    public abstract void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer);
}
