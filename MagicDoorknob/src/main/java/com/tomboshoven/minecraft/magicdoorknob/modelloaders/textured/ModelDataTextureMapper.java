package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

/**
 * Extract the texture location from extra model data.
 */
@OnlyIn(Dist.CLIENT)
class ModelDataTextureMapper implements ITextureMapper {
    @Override
    public Material mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable ModelData extraData) {
        if (extraData != null) {
            ResourceLocation name = spriteToMap.getName();
            ModelProperty<Material> modelProperty = ModelTextureProperty.get(name);
            Material material = extraData.get(modelProperty);
            if (material != null) {
                return material;
            }
        }
        return new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation());
    }
}
