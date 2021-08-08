package com.tomboshoven.minecraft.magicmirror.reflection.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

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
        entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
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
    public void setUp() {
        // Re-initialize the projection matrix to keep full control over the perspective
        RenderSystem.backupProjectionMatrix();
        // Aspect is .5 to compensate for the rectangular mirror
        RenderSystem.setProjectionMatrix(Matrix4f.perspective(90f, .5f, .05f, 50f));
    }

    @Override
    public void tearDown() {
        // Simply pop the projection matrix
        RenderSystem.restoreProjectionMatrix();
    }

    @Override
    public void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer) {
        if (entityRenderer == null) {
            return;
        }

        PoseStack reflectionMatrixStack = new PoseStack();

        // Head's up
        reflectionMatrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
        // Position within the frame
        reflectionMatrixStack.translate(0, -1, 1.5);
        // Face toward the front of the mirror
        reflectionMatrixStack.mulPose(Vector3f.YP.rotationDegrees(facing));

        // The typing of these classes works out a little weird, so instead of complicating things too much, let's go
        // with the unchecked cast.
        //noinspection unchecked
        ((EntityRenderer<Entity>) entityRenderer).render(entity, 0, partialTicks, reflectionMatrixStack, renderTypeBuffer, 0x00f000f0);
    }
}
