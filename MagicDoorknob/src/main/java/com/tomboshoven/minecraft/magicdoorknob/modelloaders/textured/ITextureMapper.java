package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ITextureMapper {
    ResourceLocation mapSprite(PropertySprite spriteToMap, @Nullable IExtendedBlockState blockState);
}
