package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for providing texture mappers for item stacks.
 */
public interface ItemStackTextureMapperProvider {
    /**
     * @param stack The item stack to provide a texture mapper for
     * @return A texture mapper
     */
    TextureMapper getTextureMapper(ItemStack stack);
}
