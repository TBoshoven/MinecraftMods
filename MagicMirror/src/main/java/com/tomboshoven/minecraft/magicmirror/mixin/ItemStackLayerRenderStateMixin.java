package com.tomboshoven.minecraft.magicmirror.mixin;

import com.tomboshoven.minecraft.magicmirror.client.renderers.OffModelRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.sprite.Material;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

/**
 * Mixin for exposing copying functionality for item stack render state layers, so they can be used in reflections.
 */
@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface ItemStackLayerRenderStateMixin extends OffModelRenderer.CopyableItemStackRenderState.CopyableLayerRenderState {
    @Accessor
    boolean getUsesBlockLight();

    @Accessor
    Material.@Nullable Baked getParticleMaterial();

    @Accessor
    ItemTransform getItemTransform();

    @Accessor
    Matrix4f getLocalTransform();

    @Accessor
    ItemStackRenderState.FoilType getFoilType();

    @Accessor
    void setFoilType(ItemStackRenderState.FoilType foilType);

    @Accessor
    @Nullable
    SpecialModelRenderer<Object> getSpecialRenderer();

    @Accessor
    @Nullable
    Object getArgumentForSpecialRendering();

    @Accessor
    Supplier<Vector3fc[]> getExtents();
}
