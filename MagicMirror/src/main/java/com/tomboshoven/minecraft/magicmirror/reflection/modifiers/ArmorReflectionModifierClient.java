package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.ArmorMagicMirrorTileEntityModifier.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.ArmorReflectionRendererModifier;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes the armor that the reflected character is wearing.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ArmorReflectionModifierClient extends ArmorReflectionModifier {
    /**
     * @param replacementArmor : The armor to show instead of the actual armor.
     */
    public ArmorReflectionModifierClient(ReplacementArmor replacementArmor) {
        super(replacementArmor);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        return new ArmorReflectionRendererModifier(reflectionRenderer, replacementArmor);
    }
}
