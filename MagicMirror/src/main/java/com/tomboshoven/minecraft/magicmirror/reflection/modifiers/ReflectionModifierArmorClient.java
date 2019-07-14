package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.ReflectionRendererModifierArmor;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes the armor that the reflected character is wearing.
 */
@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ReflectionModifierArmorClient extends ReflectionModifierArmor {
    /**
     * @param replacementArmor : The armor to show instead of the actual armor.
     */
    private ReflectionModifierArmorClient(ReplacementArmor replacementArmor) {
        super(replacementArmor);
    }

    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        return new ReflectionRendererModifierArmor(reflectionRenderer, replacementArmor);
    }

    /**
     * Factory for the modifier, used for creating the proper objects on server-side and client-side.
     * <p>
     * This factory is intended for clients only. See ReflectionModifierArmor.Factory for the server version.
     */
    @SuppressWarnings("unused")
    public static class Factory extends ReflectionModifierArmor.Factory {
        @Override
        public ReflectionModifierArmor createClient(ReplacementArmor replacementArmor) {
            return new ReflectionModifierArmorClient(replacementArmor);
        }
    }
}
