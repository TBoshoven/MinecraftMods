package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes which entity model is rendered.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreatureReflectionModifier extends ReflectionModifier {
    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // Nothing to do on the server side
        return reflectionRenderer;
    }
}
