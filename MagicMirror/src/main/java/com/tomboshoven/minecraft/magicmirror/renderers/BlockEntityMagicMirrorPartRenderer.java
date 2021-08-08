package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorPartTileEntity;
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
class BlockEntityMagicMirrorPartRenderer extends BlockEntityMagicMirrorRendererBase implements BlockEntityRenderer<MagicMirrorPartTileEntity> {

    BlockEntityMagicMirrorPartRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(MagicMirrorPartTileEntity magicMirrorPartTileEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        MagicMirrorCoreTileEntity core = magicMirrorPartTileEntity.getCore();
        if (core != null) {
            render(core.getReflection(), magicMirrorPartTileEntity.getBlockPos(), core.getBlockState().getValue(HorizontalDirectionalBlock.FACING), poseStack, multiBufferSource);
        }
    }

    @Override
    protected boolean isTop() {
        return true;
    }
}
