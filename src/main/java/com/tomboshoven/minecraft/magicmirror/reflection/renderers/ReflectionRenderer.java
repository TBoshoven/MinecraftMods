package com.tomboshoven.minecraft.magicmirror.reflection.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

/**
 * Renderer for reflections in the mirror.
 */
public class ReflectionRenderer extends ReflectionRendererBase {
    /**
     * The entity that is rendered.
     */
    private final Entity entity;

    /**
     * The renderer class for the entity that is being rendered.
     */
    private final Render<Entity> entityRenderer;

    /**
     * @param entity The entity to render.
     */
    public ReflectionRenderer(Entity entity) {
        this.entity = entity;
        entityRenderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void render(float facing, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.loadIdentity();

        GlStateManager.rotate(180, 1, 0, 0);
        // Compensate for the rectangular texture
        GlStateManager.scale(3, 1, 1);

        GlStateManager.translate(0, 0, 2);
        GlStateManager.rotate(facing, 0, 1, 0);

        if (entityRenderer != null) {
            entityRenderer.doRender(entity, 0, -1, 0, 0, partialTicks);
        }

        GlStateManager.popMatrix();
    }
}
