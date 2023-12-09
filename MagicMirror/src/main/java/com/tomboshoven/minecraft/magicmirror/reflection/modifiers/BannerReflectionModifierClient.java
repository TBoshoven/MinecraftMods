package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.BannerReflectionRendererModifier;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public class BannerReflectionModifierClient extends BannerReflectionModifier {
    public BannerReflectionModifierClient(Collection<? extends Pair<BannerPattern, DyeColor>> patternList) {
        super(patternList);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        super.apply(reflectionRenderer);
        return new BannerReflectionRendererModifier(reflectionRenderer, getPatternList());
    }
}
