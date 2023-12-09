package com.tomboshoven.minecraft.magicmirror.reflection.renderers;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base class for reflection renderers.
 */
@SideOnly(Side.CLIENT)
public abstract class ReflectionRendererBase {
    /**
     * @return The entity that is being rendered. This is used for chaining modifiers.
     */
    public abstract Entity getEntity();

    /**
     * @return The entity renderer that is used for rendering the entity. This is used for chaining modifiers.
     */
    public abstract Render<? extends Entity> getRenderer();

    public abstract void setRenderer(Render<? extends Entity> renderer);

    /**
     * Render the reflection.
     *
     * @param facing       The rotation (in degrees) for the entity that is being rendered.
     * @param partialTicks The partial ticks, for smooth rendering.
     */
    public abstract void render(float facing, float partialTicks);
}
