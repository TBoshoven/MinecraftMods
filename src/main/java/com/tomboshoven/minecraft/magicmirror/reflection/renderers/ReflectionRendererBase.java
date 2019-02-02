package com.tomboshoven.minecraft.magicmirror.reflection.renderers;

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
     * Render the reflection.
     *
     * @param facing       The rotation (in degrees) for the entity that is being rendered.
     * @param partialTicks The partial ticks, for smooth rendering.
     */
    public abstract void render(float facing, float partialTicks);
}
