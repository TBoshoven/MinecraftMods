package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerReflectionRendererModifier extends ReflectionRendererModifier {
    /**
     * The distance at which to render the background.
     */
    private static final double BACKGROUND_DISTANCE = 16;

    // Texture coordinates of the rendered banner in the banner texture (normalized to range 0..16)
    private static final float BANNER_TEXTURE_START_U = 0, BANNER_TEXTURE_END_U = 22f / 64f * 16f;
    private static final float BANNER_TEXTURE_START_V = 0, BANNER_TEXTURE_END_V = 41f / 64f * 16f;

    private final ImmutableList<Pair<BannerPattern, DyeColor>> patternColorList;

    public BannerReflectionRendererModifier(ReflectionRendererBase baseRenderer, Collection<Pair<BannerPattern, DyeColor>> patternColorList) {
        super(baseRenderer);

        this.patternColorList = ImmutableList.copyOf(patternColorList);
    }

    @Override
    public void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer) {
        super.render(facing, partialTicks, renderTypeBuffer);

        for (Pair<BannerPattern, DyeColor> patternColor : patternColorList) {
            float[] rgb = patternColor.getRight().getTextureDiffuseColors();
            Material material = new Material(Sheets.BANNER_SHEET, patternColor.getLeft().location(true));

            VertexConsumer buffer = material.buffer(renderTypeBuffer, RenderType::entityNoOutline);

            TextureAtlasSprite sprite = material.sprite();

            // Draw a simple quad
            // Perspective is 90 degrees, so width should be distance for a perfect fit
            buffer.vertex(-BACKGROUND_DISTANCE / 2, -BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).color(rgb[0], rgb[1], rgb[2], 1).uv(sprite.getU(BANNER_TEXTURE_START_U), sprite.getV(BANNER_TEXTURE_START_V)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0x00f000f0).normal(0, 0, 1).endVertex();
            buffer.vertex(BACKGROUND_DISTANCE / 2, -BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).color(rgb[0], rgb[1], rgb[2], 1).uv(sprite.getU(BANNER_TEXTURE_END_U), sprite.getV(BANNER_TEXTURE_START_V)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0x00f000f0).normal(0, 0, 1).endVertex();
            buffer.vertex(BACKGROUND_DISTANCE / 2, BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).color(rgb[0], rgb[1], rgb[2], 1).uv(sprite.getU(BANNER_TEXTURE_END_U), sprite.getV(BANNER_TEXTURE_END_V)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0x00f000f0).normal(0, 0, 1).endVertex();
            buffer.vertex(-BACKGROUND_DISTANCE / 2, BACKGROUND_DISTANCE, -BACKGROUND_DISTANCE).color(rgb[0], rgb[1], rgb[2], 1).uv(sprite.getU(BANNER_TEXTURE_START_U), sprite.getV(BANNER_TEXTURE_END_V)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(0x00f000f0).normal(0, 0, 1).endVertex();
        }
    }
}
