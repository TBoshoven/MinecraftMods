package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;

import javax.annotation.Nullable;

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
    OffModelRenderer(EntityRenderer<SourceEntity, SourceState> sourceRenderer, EntityRenderer<TargetEntity, TargetState> targetRenderer, RenderStateWrapper<SourceState, CombinedState> wrapper, RenderStateMapper<SourceState, TargetState> mapper) {
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
    interface RenderStateWrapper<SourceState extends EntityRenderState, CombinedState extends EntityRenderState & OffModelRenderer.RenderStateHolder<SourceState>> {
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

    /**
     * Interface for item stack render states that can be copied.
     * Used for interacting with a mixin.
     *
     * @see com.tomboshoven.minecraft.magicmirror.mixin.ItemStackRenderStateMixin
     */
    public interface CopyableItemStackRenderState {
        ItemDisplayContext getDisplayContext();

        void setDisplayContext(ItemDisplayContext displayContext);

        @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
        boolean getIsLeftHand();

        void setIsLeftHand(boolean isLeftHand);

        int getActiveLayerCount();

        void setActiveLayerCount(int activeLayerCount);

        ItemStackRenderState.LayerRenderState[] getLayers();

        void ensureCapacity(int extraSlots);

        /**
         * Copy the contents of an item render state into this item render state.
         *
         * @param other The item render state to copy from.
         */
        default void copyFrom(OffModelRenderer.CopyableItemStackRenderState other) {
            setDisplayContext(other.getDisplayContext());
            setIsLeftHand(other.getIsLeftHand());
            int newLayerCount = other.getActiveLayerCount();
            ensureCapacity(newLayerCount - getActiveLayerCount());
            setActiveLayerCount(newLayerCount);
            ItemStackRenderState.LayerRenderState[] layers = getLayers();
            ItemStackRenderState.LayerRenderState[] otherLayers = other.getLayers();
            for (int i = 0; i < newLayerCount; ++i) {
                if (layers[i] instanceof CopyableLayerRenderState layer) {
                    if (otherLayers[i] instanceof CopyableLayerRenderState otherLayer) {
                        layer.copyFrom(otherLayer);
                    }
                }
            }
        }

        /**
         * Interface for item stack render state layers that can be copied.
         * Used for interacting with a mixin.
         *
         * @see com.tomboshoven.minecraft.magicmirror.mixin.ItemStackLayerRenderStateMixin
         */
        @SuppressWarnings("InterfaceNeverImplemented")
        interface CopyableLayerRenderState {
            @Nullable
            BakedModel getModel();

            void setModel(@Nullable BakedModel model);

            @Nullable
            RenderType getRenderType();

            void setRenderType(@Nullable RenderType renderType);

            ItemStackRenderState.FoilType getFoilType();

            void setFoilType(ItemStackRenderState.FoilType foilType);

            int[] prepareTintLayers(int length);

            @Nullable
            SpecialModelRenderer<Object> getSpecialRenderer();

            void setSpecialRenderer(@Nullable SpecialModelRenderer<Object> specialRenderer);

            @Nullable
            Object getArgumentForSpecialRendering();

            void setArgumentForSpecialRendering(@Nullable Object argumentForSpecialRendering);

            /**
             * Copy the contents of an item render state layer into this item render state layer.
             *
             * @param other The item render state layer to copy from.
             */
            default void copyFrom(OffModelRenderer.CopyableItemStackRenderState.CopyableLayerRenderState other) {
                setModel(other.getModel());
                setRenderType(other.getRenderType());
                setFoilType(other.getFoilType());
                int[] otherTintLayers = prepareTintLayers(0);
                int[] tintLayer = prepareTintLayers(otherTintLayers.length);
                System.arraycopy(otherTintLayers, 0, tintLayer, 0, otherTintLayers.length);
                setSpecialRenderer(other.getSpecialRenderer());
                setArgumentForSpecialRendering(other.getArgumentForSpecialRendering());
            }
        }
    }
}
