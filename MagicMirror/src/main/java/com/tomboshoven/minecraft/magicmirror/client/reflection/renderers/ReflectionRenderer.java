package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CachedPerspectiveProjectionMatrixBuffer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;

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
     * The projection matrix to use inside the reflection.
     */
    private final CachedPerspectiveProjectionMatrixBuffer cachedProjectionMatrixBuffer = new CachedPerspectiveProjectionMatrixBuffer("Reflection", .05f, 50f);

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
        RenderSystem.setProjectionMatrix(cachedProjectionMatrixBuffer.getBuffer(16, 32, 90), ProjectionType.PERSPECTIVE);
    }

    @Override
    public void tearDown() {
        // Simply pop the projection matrix
        RenderSystem.restoreProjectionMatrix();
    }

    @Override
    public EntityRenderState updateState(float partialTicks) {
        return statefulRenderer.updateState(entity, partialTicks);
    }

    @Override
    public void submit(float facing, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        PoseStack reflectionMatrixStack = new PoseStack();

        // Head's up
        reflectionMatrixStack.mulPose(Axis.XP.rotationDegrees(180));
        // Position within the frame
        reflectionMatrixStack.translate(0, -1, 1.5);
        // Face toward the front of the mirror
        reflectionMatrixStack.mulPose(Axis.YP.rotationDegrees(facing));

        statefulRenderer.submit(reflectionMatrixStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public void close() {
        cachedProjectionMatrixBuffer.close();
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
         * @return The updated state, for further manipulation.
         */
        S updateState(E entity, float partialTicks) {
            renderer.extractRenderState(entity, state, partialTicks);
            return state;
        }

        /**
         * Render the entity using its accompanying state for rendering.
         *
         * @param poseStack           The pose stack for the render operation.
         * @param submitNodeCollector The collector to submit to.
         * @param cameraRenderState   The camera render state to use for rendering.
         */
        void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
            renderer.submit(state, poseStack, submitNodeCollector, cameraRenderState);
        }
    }
}
