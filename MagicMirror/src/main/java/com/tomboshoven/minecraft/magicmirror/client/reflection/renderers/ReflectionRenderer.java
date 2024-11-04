package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

/**
 * Renderer for reflections in the mirror.
 */
public class ReflectionRenderer<E extends Entity> extends ReflectionRendererBase<E> {
    /**
     * The entity that is rendered.
     */
    private final E entity;

    /**
     * The renderer class for the entity that is being rendered.
     */
    private StatefulRenderer<E, ? extends EntityRenderer<? super E, ?>, ?> statefulRenderer;

    /**
     * @param entity The entity to render.
     */
    public ReflectionRenderer(E entity) {
        this.entity = entity;
        EntityRenderer<? super E, ?> entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        statefulRenderer = new StatefulRenderer<>(entityRenderer);
    }

    @Override
    public E getEntity() {
        return entity;
    }

    @Override
    public void replaceRenderer(EntityRenderer<? super E, ?> renderer) {
        statefulRenderer = new StatefulRenderer<>(renderer);
    }

    @Override
    public void setUp() {
        // Re-initialize the projection matrix to keep full control over the perspective
        RenderSystem.backupProjectionMatrix();
        // Aspect is .5 to compensate for the rectangular mirror
        RenderSystem.setProjectionMatrix(new Matrix4f().setPerspective((float)(Math.PI / 2), .5f, .05f, 50f), ProjectionType.PERSPECTIVE);
    }

    @Override
    public void tearDown() {
        // Simply pop the projection matrix
        RenderSystem.restoreProjectionMatrix();
    }

    @Override
    public void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer) {
        if (statefulRenderer == null) {
            return;
        }

        PoseStack reflectionMatrixStack = new PoseStack();

        // Head's up
        reflectionMatrixStack.mulPose(Axis.XP.rotationDegrees(180));
        // Position within the frame
        reflectionMatrixStack.translate(0, -1, 1.5);
        // Face toward the front of the mirror
        reflectionMatrixStack.mulPose(Axis.YP.rotationDegrees(facing));

        statefulRenderer.updateState(entity, partialTicks);
        statefulRenderer.render(reflectionMatrixStack, renderTypeBuffer);
    }

    /**
     * The renderer + state combination representing what is being rendered.
     */
    static class StatefulRenderer<E extends Entity, R extends EntityRenderer<? super E, S>, S extends EntityRenderState> {
        private final R renderer;
        private final S state;

        /**
         * @param renderer The renderer to use for the entity.
         */
        StatefulRenderer(R renderer) {
            this.renderer = renderer;
            state = renderer.createRenderState();
        }

        /**
         * Update the render state based on an entity.
         *
         * @param entity       The entity to read from.
         * @param partialTicks The partial ticks to use for the state.
         */
        public void updateState(E entity, float partialTicks) {
            renderer.extractRenderState(entity, state, partialTicks);
        }

        /**
         * Render the entity using its accompanying state.
         *
         * @param poseStack        The pose stack for the render operation.
         * @param renderTypeBuffer The buffers to render to,
         */
        public void render(PoseStack poseStack, MultiBufferSource renderTypeBuffer) {
            renderer.render(state, poseStack, renderTypeBuffer, 0x00f000f0);
        }
    }
}
