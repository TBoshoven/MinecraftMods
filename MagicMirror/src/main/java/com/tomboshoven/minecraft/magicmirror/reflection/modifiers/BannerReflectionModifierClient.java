package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.BannerReflectionRendererModifier;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerReflectionModifierClient extends BannerReflectionModifier {
    public BannerReflectionModifierClient(Collection<? extends Pair<BannerPattern, DyeColor>> patternList) {
        super(patternList);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        super.apply(reflectionRenderer);
        return new BannerReflectionRendererModifier(reflectionRenderer, patternList);
    }
}
