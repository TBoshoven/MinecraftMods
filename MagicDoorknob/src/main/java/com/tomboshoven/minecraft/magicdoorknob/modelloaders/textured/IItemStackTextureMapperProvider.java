package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Interface for providing texture mappers for item stacks.
 */
public interface IItemStackTextureMapperProvider {
    /**
     * @param stack The item stack to provide a texture mapper for
     * @return A texture mapper
     */
    @OnlyIn(Dist.CLIENT)
    ITextureMapper getTextureMapper(ItemStack stack);
}
