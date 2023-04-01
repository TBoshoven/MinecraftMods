package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Renderer for the Magic Mirror block entity.
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class BlockEntityMagicMirrorCoreRenderer extends BlockEntityMagicMirrorRendererBase implements BlockEntityRenderer<MagicMirrorCoreBlockEntity> {
    BlockEntityMagicMirrorCoreRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(MagicMirrorCoreBlockEntity magicMirrorCoreBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        render(magicMirrorCoreBlockEntity.getReflection(), magicMirrorCoreBlockEntity.getBlockPos(), magicMirrorCoreBlockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING), poseStack, multiBufferSource, combinedLight);
    }

    @Override
    protected boolean isTop() {
        return false;
    }
}
