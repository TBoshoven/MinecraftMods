package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.google.common.collect.ImmutableList;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

/**
 * A reflection modifier that changes the background of the reflection.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerReflectionModifier extends ReflectionModifier {
    final ImmutableList<Pair<BannerPattern, DyeColor>> patternList;

    public BannerReflectionModifier(Collection<? extends Pair<BannerPattern, DyeColor>> patternList) {
        this.patternList = ImmutableList.copyOf(patternList);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // Nothing to do on the server side
        return reflectionRenderer;
    }
}
