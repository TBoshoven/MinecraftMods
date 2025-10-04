package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerReflectionRendererModifier<E extends Entity> extends ReflectionRendererModifier<E> {
    /**
     * The distance at which to render the background.
     */
    private static final float BACKGROUND_DISTANCE = 16;

    // Texture coordinates of the rendered banner in the banner texture
    private static final float BANNER_TEXTURE_START_U = 0, BANNER_TEXTURE_END_U = 22f / 64f;
    private static final float BANNER_TEXTURE_START_V = 0, BANNER_TEXTURE_END_V = 41f / 64f;

    private final DyeColor baseColor;
    private final BannerPatternLayers bannerPatternLayers;

    public BannerReflectionRendererModifier(ReflectionRendererBase<E> baseRenderer, DyeColor baseColor, BannerPatternLayers bannerPatternLayers) {
        super(baseRenderer);

        this.baseColor = baseColor;
        this.bannerPatternLayers = bannerPatternLayers;
    }

    @Override
    public void submit(float facing, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        super.submit(facing, submitNodeCollector, cameraRenderState);

        PoseStack poseStack = new PoseStack();
        AtlasManager atlasManager = Minecraft.getInstance().getAtlasManager();
        Material baseMaterial = Sheets.BANNER_BASE;
        RenderType baseRenderType = baseMaterial.renderType(RenderType::entityNoOutline);
        TextureAtlasSprite baseSprite = atlasManager.get(baseMaterial);

        submitNodeCollector.submitCustomGeometry(poseStack, baseRenderType, (pose, vertexConsumer) -> submitLayer(vertexConsumer, baseSprite, baseColor));

        for (BannerPatternLayers.Layer layer : bannerPatternLayers.layers()) {
            Holder<BannerPattern> bannerPattern = layer.pattern();
            Material layerMaterial = Sheets.getBannerMaterial(bannerPattern);
            RenderType layerRenderType = layerMaterial.renderType(RenderType::entityNoOutline);
            TextureAtlasSprite layerSprite = atlasManager.get(layerMaterial);

            submitNodeCollector.submitCustomGeometry(poseStack, layerRenderType, (pose, vertexConsumer) -> submitLayer(vertexConsumer, layerSprite, layer.color()));
        }
    }

    private static void submitLayer(VertexConsumer vertexConsumer, TextureAtlasSprite sprite, DyeColor color) {
        int rgb = color.getTextureDiffuseColor();

        // Draw a simple quad
        // Perspective is 90 degrees, so width should be distance for a perfect fit
        float startU = sprite.getU(BANNER_TEXTURE_START_U);
        float startV = sprite.getV(BANNER_TEXTURE_START_V);
        float endU = sprite.getU(BANNER_TEXTURE_END_U);
        float endV = sprite.getV(BANNER_TEXTURE_END_V);
        vertexConsumer.addVertex(-BACKGROUND_DISTANCE / 2, -BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).setColor(rgb).setUv(startU, startV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00f000f0).setNormal(0, 0, 1);
        vertexConsumer.addVertex(BACKGROUND_DISTANCE / 2, -BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).setColor(rgb).setUv(endU, startV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00f000f0).setNormal(0, 0, 1);
        vertexConsumer.addVertex(BACKGROUND_DISTANCE / 2, BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).setColor(rgb).setUv(endU, endV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00f000f0).setNormal(0, 0, 1);
        vertexConsumer.addVertex(-BACKGROUND_DISTANCE / 2, BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).setColor(rgb).setUv(startU, endV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00f000f0).setNormal(0, 0, 1);
    }
}
