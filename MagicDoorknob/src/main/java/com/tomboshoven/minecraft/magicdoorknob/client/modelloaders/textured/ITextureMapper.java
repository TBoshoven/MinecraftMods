package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;

/**
 * An interface for getting a texture location for a property.
 */
public interface ITextureMapper {
    /**
     * @param spriteToMap The property to get the texture location for
     * @param blockState  The block state as provided to getQuads; this is null for items
     * @param extraData   Extra model data if available
     * @return The location of the appropriate texture
     */
    ResourceLocation mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable IModelData extraData);
}
