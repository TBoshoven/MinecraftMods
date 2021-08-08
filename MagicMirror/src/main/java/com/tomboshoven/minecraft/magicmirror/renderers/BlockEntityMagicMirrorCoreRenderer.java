package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Renderer for the Magic Mirror tile entity.
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class BlockEntityMagicMirrorCoreRenderer extends BlockEntityMagicMirrorRendererBase implements BlockEntityRenderer<MagicMirrorCoreTileEntity> {
    BlockEntityMagicMirrorCoreRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(MagicMirrorCoreTileEntity magicMirrorCoreTileEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        render(magicMirrorCoreTileEntity.getReflection(), magicMirrorCoreTileEntity.getBlockPos(), magicMirrorCoreTileEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING), poseStack, multiBufferSource);
    }

    @Override
    protected boolean isTop() {
        return false;
    }
}
