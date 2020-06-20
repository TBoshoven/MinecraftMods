package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.google.common.collect.ImmutableList;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A modifier for a reflection renderer which puts a banner pattern in the background of the reflection.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerReflectionRendererModifier extends ReflectionRendererModifier {
    private final ImmutableList<BannerPattern> patternList;
    private final ImmutableList<DyeColor> colorList;
    private final String bannerPatternString;

    public BannerReflectionRendererModifier(ReflectionRendererBase baseRenderer, Collection<Pair<BannerPattern, DyeColor>> patternColorList) {
        super(baseRenderer);

        // The banner textures want separate lists for some reason
        patternList = patternColorList.stream().map(Pair::getLeft).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        colorList = patternColorList.stream().map(Pair::getRight).collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        bannerPatternString = generateBannerPatternString(patternColorList);
    }

    private static String generateBannerPatternString(Collection<? extends Pair<BannerPattern, DyeColor>> patternColorList) {
        return patternColorList.stream()
                .flatMap(patternItem -> Stream.of(patternItem.getLeft().getHashname(), Integer.toString(patternItem.getRight().getId())))
                .collect(Collectors.joining());
    }

    @Nullable
    private ResourceLocation getBannerResourceLocation() {
        return BannerTextures.BANNER_DESIGNS.getResourceLocation(bannerPatternString, patternList, colorList);
    }
}
