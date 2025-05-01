package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

import javax.annotation.Nullable;

/**
 * An interface for getting a texture location for a property.
 */
public interface TextureMapper {
    /**
     * Create a mapper for the given block state and model data.
     * Warning: model data is mutable. Do not rely on it maintaining its values.
     *
     * @param blockState The block state as provided to getQuads; this is null for items
     * @param modelData  Extra model data if available
     * @return The mapper for the block state
     */
    BlockStateTextureMapper forBlockState(@Nullable BlockState blockState, @Nullable ModelData modelData);

    interface BlockStateTextureMapper {
        /**
         * Generate a cache key for the specific state, if possible.
         * This follows the same constraints as the cache key on {@link net.neoforged.neoforge.client.extensions.BlockStateModelExtension#createGeometryKey}.
         * Essentially, each mapping should generate its own cache key, or this should return null for no caching.
         *
         * @return The mapping key, as some object implementing {@link Object#hashCode()} and {@link Object#equals(Object)}.
         */
        @Nullable
        Object getMappingKey();

        /**
         * @param spriteToMap The property to get the texture location for.
         * @return The appropriate material.
         */
        @Nullable
        Material mapSprite(PropertySprite spriteToMap);

        /**
         * A very basic implementation of a texture mapper that never maps any textures.
         */
        final class Empty implements BlockStateTextureMapper {
            private static final Object KEY = new Object();

            @Override
            public Object getMappingKey() {
                return KEY;
            }

            @Nullable
            @Override
            public Material mapSprite(PropertySprite spriteToMap) {
                return null;
            }
        }
    }
}
