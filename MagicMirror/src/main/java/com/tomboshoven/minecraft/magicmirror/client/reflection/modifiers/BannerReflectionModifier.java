package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.BannerMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers.BannerReflectionRendererModifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerReflectionModifier extends ReflectionModifier {
    @Override
    public <E extends Entity> ReflectionRendererBase<E> apply(MagicMirrorBlockEntityModifier modifier, ReflectionRendererBase<E> reflectionRenderer) {
        if (modifier instanceof BannerMagicMirrorBlockEntityModifier bannerModifier) {
            DyeColor baseColor = bannerModifier.getBaseColor();
            BannerPatternLayers patterns = bannerModifier.getPatterns();
            if (baseColor != null && patterns != null) {
                return new BannerReflectionRendererModifier<>(reflectionRenderer, baseColor, patterns);
            }
        }
        return reflectionRenderer;
    }
}
