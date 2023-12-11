package com.tomboshoven.minecraft.magicmirror.reflection.renderers;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;

/**
 * Base class for reflection renderers.
 */
public abstract class ReflectionRendererBase {
    /**
     * @return The entity that is being rendered. This is used for chaining modifiers.
     */
    public abstract Entity getEntity();

    /**
     * @return The entity renderer that is used for rendering the entity. This is used for chaining modifiers.
     */
    public abstract EntityRenderer<? extends Entity> getRenderer();

    public abstract void setRenderer(EntityRenderer<? extends Entity> renderer);

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
    public abstract void render(float facing, float partialTicks, IRenderTypeBuffer renderTypeBuffer);
}
