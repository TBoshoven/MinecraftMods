package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.BannerMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.BannerReflectionRendererModifier;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class BannerReflectionModifier extends ReflectionModifier {
    @Override
    public ReflectionRendererBase apply(MagicMirrorBlockEntityModifier modifier, ReflectionRendererBase reflectionRenderer) {
        if (modifier instanceof BannerMagicMirrorBlockEntityModifier bannerModifier) {
            List<Pair<Holder<BannerPattern>, DyeColor>> patternList = bannerModifier.getPatternList();
            if (patternList != null) {
                return new BannerReflectionRendererModifier(reflectionRenderer, patternList);
            }
        }
        return reflectionRenderer;
    }
}
