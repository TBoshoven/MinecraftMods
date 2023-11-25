package com.tomboshoven.minecraft.magicmirror.reflection.renderers;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Base class for reflection renderers.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
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
     * @param facing           The rotation (in degrees) for the entity that is being rendered.
     * @param partialTicks     The partial ticks, for smooth rendering.
     * @param renderTypeBuffer The buffer to render to.
     * @param colorize         If not null, render the entity in a specific color.
     */
    public abstract void render(float facing, float partialTicks, MultiBufferSource.BufferSource renderTypeBuffer, @Nullable float[] colorize);

    /**
     * Render the reflection.
     *
     * @param facing           The rotation (in degrees) for the entity that is being rendered.
     * @param partialTicks     The partial ticks, for smooth rendering.
     * @param renderTypeBuffer The buffer to render to.
     */
    public void render(float facing, float partialTicks, MultiBufferSource.BufferSource renderTypeBuffer) {
        render(facing, partialTicks, renderTypeBuffer, null);
    }
}
