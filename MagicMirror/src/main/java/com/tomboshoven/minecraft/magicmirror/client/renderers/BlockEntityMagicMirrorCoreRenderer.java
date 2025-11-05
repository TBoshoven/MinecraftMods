package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Renderer for the Magic Mirror block entity.
 */
class BlockEntityMagicMirrorCoreRenderer extends BlockEntityMagicMirrorRendererBase<MagicMirrorCoreBlockEntity> {
    BlockEntityMagicMirrorCoreRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(MagicMirrorCoreBlockEntity blockEntity, RenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);
        extractRenderState(blockEntity, renderState);
    }

    @Override
    protected boolean isTop() {
        return false;
    }
}
