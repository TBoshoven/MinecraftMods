package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.properties.PropertyTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class BlockStateTextureMapper implements ITextureMapper {
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
