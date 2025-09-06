package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.TextureSourceReference;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

/**
 * Extract the texture location from extra model data.
 */
class ModelDataTextureMapper implements ITextureMapper {
    @Override
    public TextureSourceReference mapSprite(PropertySprite spriteToMap, @Nullable BlockState blockState, @Nullable ModelData extraData) {
        if (extraData != null) {
            ResourceLocation name = spriteToMap.contents().name();
            ModelProperty<TextureSourceReference> modelProperty = ModelTextureProperty.get(name);
            TextureSourceReference textureSourceReference = extraData.get(modelProperty);
            if (textureSourceReference != null) {
                return textureSourceReference;
            }
        }
        return new TextureSourceReference.MaterialTextureSource(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
    }
}
