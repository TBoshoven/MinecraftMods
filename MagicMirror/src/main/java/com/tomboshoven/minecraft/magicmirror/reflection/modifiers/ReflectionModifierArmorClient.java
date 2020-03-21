package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.ReflectionRendererModifierArmor;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes the armor that the reflected character is wearing.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ReflectionModifierArmorClient extends ReflectionModifierArmor {
    /**
     * @param replacementArmor : The armor to show instead of the actual armor.
     */
    public ReflectionModifierArmorClient(ReplacementArmor replacementArmor) {
        super(replacementArmor);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        return new ReflectionRendererModifierArmor(reflectionRenderer, replacementArmor);
    }
}
