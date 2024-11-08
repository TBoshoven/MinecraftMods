package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.ReflectionManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

/**
 * Renderer for the Magic Mirror block entity.
 */
class BlockEntityMagicMirrorCoreRenderer extends BlockEntityMagicMirrorRendererBase implements BlockEntityRenderer<MagicMirrorCoreBlockEntity> {
    BlockEntityMagicMirrorCoreRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MagicMirrorCoreBlockEntity magicMirrorCoreBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        Reflection reflection = ReflectionManager.reflectionForRendering(magicMirrorCoreBlockEntity, renderContext());
        render(reflection, magicMirrorCoreBlockEntity.getBlockPos(), magicMirrorCoreBlockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING), poseStack, multiBufferSource, combinedLight);
    }

    @Override
    protected boolean isTop() {
        return false;
    }
}
