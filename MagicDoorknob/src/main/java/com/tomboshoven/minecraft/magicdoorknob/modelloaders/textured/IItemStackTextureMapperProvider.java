package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    @OnlyIn(Dist.CLIENT)
    ITextureMapper getTextureMapper(ItemStack stack);
}
