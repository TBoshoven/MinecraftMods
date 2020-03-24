package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An interface for getting a texture location for a property.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public interface ITextureMapper {
    /**
     * @param spriteToMap The property to get the texture location for
     * @param blockState  The block state as provided to getQuads; this is null for items
     * @param extraData   Extra model data if available
     * @return The location of the appropriate texture
     */
    ResourceLocation mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable IModelData extraData);
}
