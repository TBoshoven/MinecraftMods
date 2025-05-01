package com.tomboshoven.minecraft.magicmirror.mixin;

import com.tomboshoven.minecraft.magicmirror.client.renderers.OffModelRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixin for exposing copying functionality for item stack render states, so they can be used in reflections.
 */
@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateMixin extends OffModelRenderer.CopyableItemStackRenderState {
    @Accessor
    ItemDisplayContext getDisplayContext();

    @Accessor
    void setDisplayContext(ItemDisplayContext context);

    @Accessor
    int getActiveLayerCount();

    @Accessor
    void setActiveLayerCount(int activeLayerCount);

    @Accessor
    ItemStackRenderState.LayerRenderState[] getLayers();
}
