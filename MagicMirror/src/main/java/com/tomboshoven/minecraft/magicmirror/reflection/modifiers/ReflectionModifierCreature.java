package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;

/**
 * A reflection modifier that changes which entity model is rendered.
 */
public class ReflectionModifierCreature extends ReflectionModifier {
    @Override
    public ReflectionRendererBase apply(ReflectionRendererBase reflectionRenderer) {
        // Nothing to do on the server side
        return reflectionRenderer;
    }

    /**
     * Factory for the modifier, used for creating the proper objects on server-side and client-side.
     * <p>
     * This factory is intended for servers. See ReflectionModifierCreatureClient.Factory for the client version.
     */
    public static class Factory {
        /**
         * Create a server-side magic mirror block.
         *
         * @return A new ReflectionModifierCreature object.
         */
        @SuppressWarnings("MethodMayBeStatic")
        public ReflectionModifierCreature createServer() {
            return new ReflectionModifierCreature();
        }

        /**
         * Create a client-side magic mirror block.
         *
         * @return A new ReflectionModifierCreature object.
         */
        public ReflectionModifierCreature createClient() {
            return new ReflectionModifierCreature();
        }
    }
}
