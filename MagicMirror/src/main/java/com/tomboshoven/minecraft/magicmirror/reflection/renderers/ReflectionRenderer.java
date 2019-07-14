package com.tomboshoven.minecraft.magicmirror.reflection.renderers;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.lwjgl.util.glu.Project;

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
    private Render<? extends Entity> entityRenderer;

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
    public Render<? extends Entity> getRenderer() {
        return entityRenderer;
    }

    @Override
    public void setRenderer(Render<? extends Entity> renderer) {
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
        Project.gluPerspective(90f, .5f, .05f, 50f);
        GlStateManager.matrixMode(5888);

        GlStateManager.pushMatrix();

        GlStateManager.loadIdentity();

        GlStateManager.rotate(180, 1, 0, 0);

        GlStateManager.translate(0, 0, 1.5);
        GlStateManager.rotate(facing, 0, 1, 0);

        // The typing of these classes works out a little weird, so instead of complicating things too much, let's go
        // with the unchecked cast.
        ((Render<Entity>) entityRenderer).doRender(entity, 0, -1, 0, 0, partialTicks);

        GlStateManager.popMatrix();
        // Restore the perspective.
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
    }
}
