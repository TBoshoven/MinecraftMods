package com.tomboshoven.minecraft.magicmirror.reflection.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Renderer for reflections in the mirror.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ReflectionRenderer extends ReflectionRendererBase {
    /**
     * The entity that is rendered.
     */
    private final Entity entity;

    /**
     * The renderer class for the entity that is being rendered.
     */
    private EntityRenderer<? extends Entity> entityRenderer;

    /**
     * @param entity The entity to render.
     */
    public ReflectionRenderer(Entity entity) {
        this.entity = entity;
        entityRenderer = Minecraft.getInstance().getRenderManager().getEntityRenderObject(entity);
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public EntityRenderer<? extends Entity> getRenderer() {
        return entityRenderer;
    }

    @Override
    public void setRenderer(EntityRenderer<? extends Entity> renderer) {
        entityRenderer = renderer;
    }

    @Override
    public void render(float facing, float partialTicks) {
        if (entityRenderer == null) {
            return;
        }

        // Set the perspective to prevent FoV impacting things.
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        // Aspect is .5 to compensate for the rectangular mirror
        GlStateManager.multMatrix(Matrix4f.perspective(90f, .5f, .05f, 50f));
        GlStateManager.matrixMode(5888);

        GlStateManager.pushMatrix();

        GlStateManager.loadIdentity();

        GlStateManager.rotated(180, 1, 0, 0);

        GlStateManager.translated(0, 0, 1.5);
        GlStateManager.rotatef(facing, 0, 1, 0);

        // The typing of these classes works out a little weird, so instead of complicating things too much, let's go
        // with the unchecked cast.
        ((EntityRenderer<Entity>) entityRenderer).doRender(entity, 0, -1, 0, 0, partialTicks);

        GlStateManager.popMatrix();
        // Restore the perspective.
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
    }
}
