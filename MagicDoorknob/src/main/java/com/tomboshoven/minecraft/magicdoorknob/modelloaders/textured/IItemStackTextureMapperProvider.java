package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IItemStackTextureMapperProvider {
    ITextureMapper getTextureMapper(ItemStack stack);
}
