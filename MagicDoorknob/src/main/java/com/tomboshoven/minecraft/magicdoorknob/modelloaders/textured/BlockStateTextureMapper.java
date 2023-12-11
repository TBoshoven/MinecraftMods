package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.properties.PropertyTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Extract the texture location from an extended blockstate.
 */
@SideOnly(Side.CLIENT)
class BlockStateTextureMapper implements ITextureMapper {
    @Override
    public ResourceLocation mapSprite(PropertySprite spriteToMap, @Nullable IExtendedBlockState blockState) {
        if (blockState != null) {
            String name = spriteToMap.getIconName();
            IUnlistedProperty<ResourceLocation> property = new PropertyTexture(name);
            ResourceLocation spriteLocation = blockState.getValue(property);
            if (spriteLocation != null) {
                return spriteLocation;
            }
        }
        return new ResourceLocation("missingno");
    }
}
