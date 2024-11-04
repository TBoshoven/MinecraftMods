package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
    public void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer) {
        super.render(facing, partialTicks, renderTypeBuffer);

        VertexConsumer baseBuffer = Sheets.BANNER_BASE.buffer(renderTypeBuffer, RenderType::entityNoOutline);
        drawLayer(baseBuffer, baseColor);

        for (BannerPatternLayers.Layer layer : bannerPatternLayers.layers()) {
            Holder<BannerPattern> bannerPattern = layer.pattern();
            Material material = Sheets.getBannerMaterial(bannerPattern);

            VertexConsumer buffer = material.buffer(renderTypeBuffer, RenderType::entityNoOutline);

            drawLayer(buffer, layer.color());
        }
    }

    private void drawLayer(VertexConsumer buffer, DyeColor color) {
        int rgb = color.getTextureDiffuseColor();

        // Draw a simple quad
        // Perspective is 90 degrees, so width should be distance for a perfect fit
        buffer.addVertex(-BACKGROUND_DISTANCE / 2, -BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).setColor(rgb).setUv(BANNER_TEXTURE_START_U, BANNER_TEXTURE_START_V).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00f000f0).setNormal(0, 0, 1);
        buffer.addVertex(BACKGROUND_DISTANCE / 2, -BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).setColor(rgb).setUv(BANNER_TEXTURE_END_U, BANNER_TEXTURE_START_V).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00f000f0).setNormal(0, 0, 1);
        buffer.addVertex(BACKGROUND_DISTANCE / 2, BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).setColor(rgb).setUv(BANNER_TEXTURE_END_U, BANNER_TEXTURE_END_V).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00f000f0).setNormal(0, 0, 1);
        buffer.addVertex(-BACKGROUND_DISTANCE / 2, BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).setColor(rgb).setUv(BANNER_TEXTURE_START_U, BANNER_TEXTURE_END_V).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00f000f0).setNormal(0, 0, 1);
    }
}
