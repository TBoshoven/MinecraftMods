package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.ArmorMagicMirrorTileEntityModifier.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes the armor that the reflected character is wearing.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArmorReflectionModifier extends ReflectionModifier {
    /**
     * The armor to show instead of the actual armor.
     */
    final ReplacementArmor replacementArmor;

    /**
     * @param replacementArmor The armor to show instead of the actual armor.
     */
    public ArmorReflectionModifier(ReplacementArmor replacementArmor) {
        this.replacementArmor = replacementArmor;
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // Nothing to do on the server side
        return reflectionRenderer;
    }
}
