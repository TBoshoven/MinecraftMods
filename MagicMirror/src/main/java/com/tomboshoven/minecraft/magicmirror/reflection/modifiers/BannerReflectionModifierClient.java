package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.BannerReflectionRendererModifier;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerReflectionModifierClient extends BannerReflectionModifier {
    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        super.apply(reflectionRenderer);
        return new BannerReflectionRendererModifier(reflectionRenderer);
    }
}
