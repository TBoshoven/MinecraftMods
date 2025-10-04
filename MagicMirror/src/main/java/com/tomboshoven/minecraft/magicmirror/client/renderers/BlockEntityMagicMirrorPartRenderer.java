package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorPartBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Renderer for the Magic Mirror block entity.
 */
class BlockEntityMagicMirrorPartRenderer extends BlockEntityMagicMirrorRendererBase<MagicMirrorPartBlockEntity> {
    BlockEntityMagicMirrorPartRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(MagicMirrorPartBlockEntity blockEntity, RenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);
        MagicMirrorCoreBlockEntity coreBlockEntity = blockEntity.getCore();
        if (coreBlockEntity != null) {
            extractRenderState(coreBlockEntity, renderState);
        }
    }

    @Override
    protected boolean isTop() {
        return true;
    }
}
