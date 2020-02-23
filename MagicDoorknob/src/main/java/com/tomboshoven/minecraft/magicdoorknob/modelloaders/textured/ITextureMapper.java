package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An interface for getting a texture location for a property.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public interface ITextureMapper {
    /**
     * @param spriteToMap The property to get the texture location for
     * @param blockState  The block state as provided to getQuads; this is null for items
     * @return The location of the appropriate texture
     */
    ResourceLocation mapSprite(PropertySprite spriteToMap, @Nullable IExtendedBlockState blockState);
}
