package com.tomboshoven.minecraft.magicmirror.mixin;

import com.tomboshoven.minecraft.magicmirror.client.renderers.OffModelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

/**
 * Mixin for exposing copying functionality for item stack render state layers, so they can be used in reflections.
 */
@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface ItemStackLayerRenderStateMixin extends OffModelRenderer.CopyableItemStackRenderState.CopyableLayerRenderState {
    @Accessor
    @Nullable
    BakedModel getModel();

    @Accessor
    void setModel(@Nullable BakedModel model);

    @Accessor
    @Nullable
    RenderType getRenderType();

    @Accessor
    void setRenderType(@Nullable RenderType renderType);

    @Accessor
    ItemStackRenderState.FoilType getFoilType();

    @Accessor
    void setFoilType(ItemStackRenderState.FoilType foilType);

    @Accessor
    @Nullable
    SpecialModelRenderer<Object> getSpecialRenderer();

    @Accessor
    void setSpecialRenderer(@Nullable SpecialModelRenderer<Object> specialRenderer);

    @Accessor
    @Nullable
    Object getArgumentForSpecialRendering();

    @Accessor
    void setArgumentForSpecialRendering(@Nullable Object argumentForSpecialRendering);
}
