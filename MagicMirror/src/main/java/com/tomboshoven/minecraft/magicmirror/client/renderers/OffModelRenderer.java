package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

/**
 * A renderer to render an entity using a different model.
 *
 * @param <SourceEntity>  The type of entity we want to render as something else.
 * @param <SourceState>   The render state used for rendering the source entity.
 * @param <TargetEntity>  The type of entity we would like to show instead of the actual entity.
 * @param <TargetState>   The render state used for rendering the target entity.
 * @param <CombinedState> The combined render state. Must inherit from the target state and also contain the source state.
 */
public class OffModelRenderer<SourceEntity extends Entity, SourceState extends EntityRenderState, TargetEntity extends Entity, TargetState extends EntityRenderState, CombinedState extends EntityRenderState & OffModelRenderer.RenderStateHolder<SourceState>> {
    /**
     * The renderer for the source entity. Used only for extracting state information from the entity.
     */
    private final EntityRenderer<SourceEntity, SourceState> sourceRenderer;
    /**
     * The renderer for the target entity. Used only for rendering the existing state.
     */
    private final EntityRenderer<TargetEntity, TargetState> targetRenderer;
    /**
     * Function for turning the source state into the combined state.
     */
    private final RenderStateWrapper<SourceState, CombinedState> wrapper;
    /**
     * Mapping function from source to target state.
     */
    private final RenderStateMapper<SourceState, TargetState> mapper;

    /**
     * @param sourceRenderer The renderer for the source entity.
     * @param targetRenderer The renderer for the target entity.
     * @param wrapper        Function for turning the source state into the combined state.
     * @param mapper         Mapping function from source to target state.
     */
    public OffModelRenderer(EntityRenderer<SourceEntity, SourceState> sourceRenderer, EntityRenderer<TargetEntity, TargetState> targetRenderer, RenderStateWrapper<SourceState, CombinedState> wrapper, RenderStateMapper<SourceState, TargetState> mapper) {
        this.sourceRenderer = sourceRenderer;
        this.targetRenderer = targetRenderer;
        this.wrapper = wrapper;
        this.mapper = mapper;
    }

    /**
     * Create the actual renderer.
     * This is two-step process to work around a Java initialization-order limitation.
     *
     * @param renderContext The render context for the off-model entity renderer.
     * @return The fully-initialized renderer.
     */
    public Renderer create(EntityRendererProvider.Context renderContext) {
        return new Renderer(renderContext);
    }

    // Nested class structure is a hack
    // The issue is that sourceRenderer must be initialized before the renderer, because createRenderState is called as
    // part of construction, and it depends on the source entity.
    /**
     * The off-model entity renderer.
     */
    public class Renderer extends EntityRenderer<SourceEntity, CombinedState> {
        /**
         * @param renderContext The render context for the off-model entity renderer.
         */
        Renderer(EntityRendererProvider.Context renderContext) {
            super(renderContext);
        }

        @Override
        public void render(CombinedState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            // This would require an explicit constraint that CombinedState extends TargetState.
            // While it's perfectly legitimate, Java doesn't allow specifying this.
            @SuppressWarnings("unchecked") TargetState targetState = (TargetState) renderState;
            targetRenderer.render(targetState, poseStack, bufferSource, packedLight);
        }

        @Override
        public CombinedState createRenderState() {
            return wrapper.wrap(sourceRenderer.createRenderState());
        }

        @Override
        public void extractRenderState(SourceEntity sourceEntity, CombinedState renderState, float partialTicks) {
            SourceState sourceState = renderState.getSource();
            @SuppressWarnings("unchecked") TargetState targetState = (TargetState) renderState;
            sourceRenderer.extractRenderState(sourceEntity, sourceState, partialTicks);
            mapper.mapRenderState(sourceState, targetState);
        }
    }

    /**
     * Function to transform a render state into a combined render state,
     * @param <SourceState>   The input state.
     * @param <CombinedState> The output state, which is based on a target state and also contains the source state.
     */
    public interface RenderStateWrapper<SourceState extends EntityRenderState, CombinedState extends EntityRenderState & OffModelRenderer.RenderStateHolder<SourceState>> {
        /**
         * @param state The state to be wrapped by another state.
         * @return The combined state.
         */
        CombinedState wrap(SourceState state);
    }

    /**
     * Interface for a container of a source state.
     * A typical requirement is that the container extends the target state.
     *
     * @param <SourceState> The state to hold.
     */
    public interface RenderStateHolder<SourceState extends EntityRenderState> {
        /**
         * @return The held state.
         */
        SourceState getSource();
    }

    /**
     * Mapping function from one state to another.
     * @param <SourceState> The source state to read from.
     * @param <TargetState> The target state to write to.
     */
    public interface RenderStateMapper<SourceState extends EntityRenderState, TargetState extends EntityRenderState> {
        /**
         * @param sourceState The source state to read from.
         * @param targetState The target state to write to.
         */
        void mapRenderState(SourceState sourceState, TargetState targetState);
    }
}
