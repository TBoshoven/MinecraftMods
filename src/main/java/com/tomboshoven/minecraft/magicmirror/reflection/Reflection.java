package com.tomboshoven.minecraft.magicmirror.reflection;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection of an entity.
 *
 * This class is mainly responsible for rendering this reflection and making it available as a texture.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Reflection {
    @Nullable
    private Entity entityToReflect = null;
    @Nullable
    private Framebuffer frameBuffer = null;
    @Nullable
    private Render<Entity> entityRenderer = null;
    private float facing = 0f;

    private static int activeReflections = 0;

    /**
     * Get the total number of active reflections in this instance; used for debugging leaks.
     */
    public static int getActiveReflections() {
        return activeReflections;
    }

    /**
     * Stop reflecting any entities.
     * This cleans up all used resources.
     */
    public void stopReflecting() {
        if (entityToReflect != null) {
            ModMagicMirror.logger.debug("No longer reflecting {}", entityToReflect.getName());
            if (frameBuffer != null) {
                frameBuffer.deleteFramebuffer();
                frameBuffer = null;
            }
            entityToReflect = null;
            entityRenderer = null;
            --activeReflections;
        }
    }

    /**
     * Reflect an entity.
     * @param entityToReflect: Which entity to start reflecting.
     */
    public void setEntityToReflect(Entity entityToReflect) {
        if (this.entityToReflect != entityToReflect) {
            ModMagicMirror.logger.debug("Reflecting {}", entityToReflect.getName());
            if (this.entityToReflect == null) {
                frameBuffer = new Framebuffer(64, 128, true);
                frameBuffer.unbindFramebuffer();
                ++activeReflections;
            }
            this.entityToReflect = entityToReflect;
            entityRenderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entityToReflect);
        }
    }

    /**
     * @return Which entity is currently being reflected, or null if none.
     */
    @Nullable
    public Entity getReflected() {
        return entityToReflect;
    }

    /**
     * @param facing: The way the reflection is facing; used for determining which side of the subject to show.
     */
    public void setFacing(float facing) {
        this.facing = facing;
    }

    /**
     * Render the reflection of the entity to the texture.
     * This operation unbinds the frame buffer, so rebinding may be required afterward.
     * @param partialTicks: The partial ticks, used for rendering smooth animations.
     */
    public void render(float partialTicks) {
        if (entityToReflect != null && frameBuffer != null) {
            frameBuffer.framebufferClear();
            frameBuffer.bindFramebuffer(true);

            GlStateManager.pushMatrix();

            GlStateManager.loadIdentity();

            GlStateManager.rotate(180, 1, 0, 0);
            // Compensate for the rectangular texture
            GlStateManager.scale(3, 1, 1);

            GlStateManager.translate(0, 0, 2);
            GlStateManager.rotate(facing, 0, 1, 0);

            if (entityRenderer != null) {
                entityRenderer.doRender(entityToReflect, 0, -1, 0, 0, partialTicks);
            }

            GlStateManager.popMatrix();

            frameBuffer.unbindFramebuffer();
        }
    }

    /**
     * Bind the reflection texture.
     * Before calling this, make sure there is an active reflection, and render() has been called at least once since it
     * became active.
     */
    public void bind() {
        if (frameBuffer == null) {
            throw new RuntimeException("No active reflection");
        }
        frameBuffer.bindFramebufferTexture();
    }
}
