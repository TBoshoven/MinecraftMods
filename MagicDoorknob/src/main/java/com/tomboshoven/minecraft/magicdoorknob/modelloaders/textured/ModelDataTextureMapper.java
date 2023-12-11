package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

/**
 * Extract the texture location from extra model data.
 */
class ModelDataTextureMapper implements ITextureMapper {
    @Override
    public Material mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable IModelData extraData) {
        if (extraData != null) {
            ResourceLocation name = spriteToMap.getName();
            ModelProperty<Material> modelProperty = ModelTextureProperty.get(name);
            Material material = extraData.getData(modelProperty);
            if (material != null) {
                return material;
            }
        }
        return new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation());
    }
}
