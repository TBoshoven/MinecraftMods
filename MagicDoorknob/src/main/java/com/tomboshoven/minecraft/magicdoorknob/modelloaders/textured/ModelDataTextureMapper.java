package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

/**
 * Extract the texture location from extra model data.
 */
class ModelDataTextureMapper implements ITextureMapper {
    @Override
    public RenderMaterial mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable IModelData extraData) {
        if (extraData != null) {
            ResourceLocation name = spriteToMap.getName();
            ModelProperty<RenderMaterial> modelProperty = ModelTextureProperty.get(name);
            RenderMaterial material = extraData.getData(modelProperty);
            if (material != null) {
                return material;
            }
        }
        return new RenderMaterial(PlayerContainer.BLOCK_ATLAS, MissingTextureSprite.getLocation());
    }
}
