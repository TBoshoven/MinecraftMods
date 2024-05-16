package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.BannerMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers.BannerReflectionRendererModifier;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class BannerReflectionModifier extends ReflectionModifier {
    @Override
    public ReflectionRendererBase apply(MagicMirrorBlockEntityModifier modifier, ReflectionRendererBase reflectionRenderer) {
        if (modifier instanceof BannerMagicMirrorBlockEntityModifier) {
            BannerMagicMirrorBlockEntityModifier bannerModifier = (BannerMagicMirrorBlockEntityModifier) modifier;
            List<Pair<BannerPattern, DyeColor>> patternList = bannerModifier.getPatternList();
            if (patternList != null) {
                return new BannerReflectionRendererModifier(reflectionRenderer, patternList);
            }
        }
        return reflectionRenderer;
    }
}
