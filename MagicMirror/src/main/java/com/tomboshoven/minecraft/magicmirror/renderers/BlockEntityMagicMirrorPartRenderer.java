package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorPartBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renderer for the Magic Mirror block entity.
 */
@OnlyIn(Dist.CLIENT)
class BlockEntityMagicMirrorPartRenderer extends BlockEntityMagicMirrorRendererBase implements BlockEntityRenderer<MagicMirrorPartBlockEntity> {

    BlockEntityMagicMirrorPartRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(MagicMirrorPartBlockEntity magicMirrorPartBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        MagicMirrorCoreBlockEntity core = magicMirrorPartBlockEntity.getCore();
        if (core != null) {
            render(core.getReflection(), magicMirrorPartBlockEntity.getBlockPos(), core.getBlockState().getValue(HorizontalDirectionalBlock.FACING), poseStack, multiBufferSource, combinedLight);
        }
    }

    @Override
    protected boolean isTop() {
        return true;
    }
}
