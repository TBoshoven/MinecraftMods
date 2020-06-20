package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.google.common.collect.ImmutableList;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

/**
 * A reflection modifier that changes the background of the reflection.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerReflectionModifier extends ReflectionModifier {
    private ImmutableList<Pair<BannerPattern, DyeColor>> patternList;

    public BannerReflectionModifier(Collection<? extends Pair<BannerPattern, DyeColor>> patternList) {
        this.patternList = ImmutableList.copyOf(patternList);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // Nothing to do on the server side
        return reflectionRenderer;
    }

    ImmutableList<Pair<BannerPattern, DyeColor>> getPatternList() {
        return patternList;
    }
}
