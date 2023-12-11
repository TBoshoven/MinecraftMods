package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.item.ItemStack;

/**
 * Interface for providing texture mappers for item stacks.
 */
public interface IItemStackTextureMapperProvider {
    /**
     * @param stack The item stack to provide a texture mapper for
     * @return A texture mapper
     */
    ITextureMapper getTextureMapper(ItemStack stack);
}
