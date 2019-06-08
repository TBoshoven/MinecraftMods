package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers.ReflectionRendererModifierCreature;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A reflection modifier that changes the armor that the entity used to represent the character.
 */
@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ReflectionModifierCreatureClient extends ReflectionModifierCreature {
    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        return new ReflectionRendererModifierCreature(reflectionRenderer);
    }

    /**
     * Factory for the modifier, used for creating the proper objects on server-side and client-side.
     * <p>
     * This factory is intended for clients only. See ReflectionModifierArmor.Factory for the server version.
     */
    @SuppressWarnings("unused")
    public static class Factory extends ReflectionModifierCreature.Factory {
        @Override
        public ReflectionModifierCreature createClient() {
            return new ReflectionModifierCreatureClient();
        }
    }
}
