package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;

/**
 * A reflection modifier that changes the armor that the reflected character is wearing.
 */
public class ReflectionModifierArmor extends ReflectionModifier {
    /**
     * The armor to show instead of the actual armor.
     */
    final ReplacementArmor replacementArmor;

    /**
     * @param replacementArmor The armor to show instead of the actual armor.
     */
    ReflectionModifierArmor(ReplacementArmor replacementArmor) {
        this.replacementArmor = replacementArmor;
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // Nothing to do on the server side
        return reflectionRenderer;
    }

    /**
     * Factory for the modifier, used for creating the proper objects on server-side and client-side.
     * <p>
     * This factory is intended for servers. See ReflectionModifierArmorClient.Factory for the client version.
     */
    public static class Factory {
        /**
         * Create a server-side magic mirror block.
         *
         * @param replacementArmor The armor to show instead of the actual armor.
         * @return A new ReflectionModifierArmor object.
         */
        @SuppressWarnings("MethodMayBeStatic")
        public ReflectionModifierArmor createServer(ReplacementArmor replacementArmor) {
            return new ReflectionModifierArmor(replacementArmor);
        }

        /**
         * Create a client-side magic mirror block.
         *
         * @param replacementArmor The armor to show instead of the actual armor.
         * @return A new ReflectionModifierArmor object.
         */
        public ReflectionModifierArmor createClient(ReplacementArmor replacementArmor) {
            return new ReflectionModifierArmor(replacementArmor);
        }
    }
}
