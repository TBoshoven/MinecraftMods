package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IItemStackTextureMapperProvider {
    @SideOnly(Side.CLIENT)
    ITextureMapper getTextureMapper(ItemStack stack);
}
