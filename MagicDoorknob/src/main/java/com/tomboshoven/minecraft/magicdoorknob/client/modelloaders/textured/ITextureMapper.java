package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nullable;

/**
 * An interface for getting a texture location for a property.
 */
public interface ITextureMapper {
    /**
     * @param spriteToMap The property to get the texture location for
     * @param blockState  The block state as provided to getQuads; this is null for items
     * @param extraData   Extra model data if available
     * @return The appropriate material
     */
    Material mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable ModelData extraData);
}
