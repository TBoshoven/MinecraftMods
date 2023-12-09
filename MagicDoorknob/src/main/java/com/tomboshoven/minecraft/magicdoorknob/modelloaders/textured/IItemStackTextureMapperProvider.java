package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Interface for providing texture mappers for item stacks.
 */
public interface IItemStackTextureMapperProvider {
    /**
     * @param stack The item stack to provide a texture mapper for
     * @return A texture mapper
     */
    @SideOnly(Side.CLIENT)
    ITextureMapper getTextureMapper(ItemStack stack);
}
