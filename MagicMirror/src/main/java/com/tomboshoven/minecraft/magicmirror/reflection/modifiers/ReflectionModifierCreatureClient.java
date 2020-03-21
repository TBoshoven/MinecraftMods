package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.ReflectionRendererModifierCreature;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes the armor that the entity used to represent the character.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ReflectionModifierCreatureClient extends ReflectionModifierCreature {
    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        return new ReflectionRendererModifierCreature(reflectionRenderer);
    }
}
