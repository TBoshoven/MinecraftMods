package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.TextureSourceReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

/**
 * Extract the texture location from extra model data.
 */
class ModelDataTextureMapper implements ITextureMapper {
    @Override
    public @Nullable TextureSourceReference mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable ModelData extraData) {
        if (extraData != null) {
            ResourceLocation name = spriteToMap.contents().name();
            ModelProperty<TextureSourceReference> modelProperty = ModelTextureProperty.get(name);
            return extraData.get(modelProperty);
        }
        return null;
    }
}
