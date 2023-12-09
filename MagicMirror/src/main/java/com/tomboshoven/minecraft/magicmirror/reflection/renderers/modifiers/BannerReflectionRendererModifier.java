package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.google.common.collect.ImmutableList;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.GL_QUADS;

/**
 * A modifier for a reflection renderer which puts a banner pattern in the background of the reflection.
 */
public class BannerReflectionRendererModifier extends ReflectionRendererModifier {
    /**
     * The distance at which to render the background.
     */
    private static final double BACKGROUND_DISTANCE = 16;

    // Texture coordinates of the rendered banner in the banner texture
    private static final double BANNER_TEXTURE_START_U = 0, BANNER_TEXTURE_END_U = 22.0 / 64.0;
    private static final double BANNER_TEXTURE_START_V = 0, BANNER_TEXTURE_END_V = 41.0 / 64.0;

    /**
     * The list of patterns to render (pattern colors stored separately).
     */
    private final ImmutableList<BannerPattern> patternList;
    /**
     * The list of colors for the individual patterns.
     */
    private final ImmutableList<DyeColor> colorList;
    /**
     * The identifier for the banner pattern; should match Vanilla.
     */
    private final String bannerPatternString;

    /**
     * @param baseRenderer     The renderer that is being proxied.
     * @param patternColorList A list of patterns to be rendered with their colors.
     */
    public BannerReflectionRendererModifier(ReflectionRendererBase baseRenderer, Collection<Pair<BannerPattern, DyeColor>> patternColorList) {
        super(baseRenderer);

        // The banner textures want separate lists for some reason
        patternList = patternColorList.stream().map(Pair::getLeft).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        colorList = patternColorList.stream().map(Pair::getRight).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        bannerPatternString = generateBannerPatternString(patternColorList);
    }

    /**
     * @param patternColorList A list of patterns to be rendered with their colors.
     * @return The identifier for the banner texture.
     */
    private static String generateBannerPatternString(Collection<? extends Pair<BannerPattern, DyeColor>> patternColorList) {
        return patternColorList.stream()
                .flatMap(patternItem -> Stream.of(patternItem.getLeft().getHashname(), Integer.toString(patternItem.getRight().getId())))
                .collect(Collectors.joining());
    }

    /**
     * @return The banner texture location.
     */
    @Nullable
    private ResourceLocation getBannerResourceLocation() {
        return BannerTextures.BANNER_CACHE.getTextureLocation(bannerPatternString, patternList, colorList);
    }

    @Override
    public void render(float facing, float partialTicks) {
        super.render(facing, partialTicks);

        ResourceLocation bannerLocation = getBannerResourceLocation();
        if (bannerLocation != null) {
            Minecraft.getInstance().getTextureManager().bind(bannerLocation);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();

            // Draw a simple quad
            // Perspective is 90 degrees, so width should be distance for a perfect fit
            bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.vertex(-BACKGROUND_DISTANCE / 2, BACKGROUND_DISTANCE, BACKGROUND_DISTANCE).uv(BANNER_TEXTURE_START_U, BANNER_TEXTURE_START_V).endVertex();
            bufferbuilder.vertex(BACKGROUND_DISTANCE / 2, BACKGROUND_DISTANCE, BACKGROUND_DISTANCE).uv(BANNER_TEXTURE_END_U, BANNER_TEXTURE_START_V).endVertex();
            bufferbuilder.vertex(BACKGROUND_DISTANCE / 2, -BACKGROUND_DISTANCE, BACKGROUND_DISTANCE).uv(BANNER_TEXTURE_END_U, BANNER_TEXTURE_END_V).endVertex();
            bufferbuilder.vertex(-BACKGROUND_DISTANCE / 2, -BACKGROUND_DISTANCE, BACKGROUND_DISTANCE).uv(BANNER_TEXTURE_START_U, BANNER_TEXTURE_END_V).endVertex();
            tessellator.end();
        }
    }
}
