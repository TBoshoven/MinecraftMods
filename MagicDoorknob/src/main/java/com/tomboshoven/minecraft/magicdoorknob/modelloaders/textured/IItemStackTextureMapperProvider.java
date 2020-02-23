package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for providing texture mappers for item stacks.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IItemStackTextureMapperProvider {
    /**
     * @param stack The item stack to provide a texture mapper for
     * @return A texture mapper
     */
    @SideOnly(Side.CLIENT)
    ITextureMapper getTextureMapper(ItemStack stack);
}
