package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Extract the texture location from extra model data.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
class ModelDataTextureMapper implements ITextureMapper {
    @Override
    public Material mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable ModelData extraData) {
        if (extraData != null) {
            ResourceLocation name = spriteToMap.contents().name();
            ModelProperty<Material> modelProperty = ModelTextureProperty.get(name);
            Material material = extraData.get(modelProperty);
            if (material != null) {
                return material;
            }
        }
        return new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation());
    }
}
