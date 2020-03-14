package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.properties.PropertyTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Extract the texture location from an extended blockstate.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
class ModelDataTextureMapper implements ITextureMapper {
    @Override
    public ResourceLocation mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable IModelData modelData) {
        if (modelData != null) {
            ResourceLocation name = spriteToMap.getName();
            IUnlistedProperty<ResourceLocation> property = new PropertyTexture(name);
            ResourceLocation spriteLocation = modelData.getData(property);
            if (spriteLocation != null) {
                return spriteLocation;
            }
        }
        return new ResourceLocation("missingno");
    }
}
