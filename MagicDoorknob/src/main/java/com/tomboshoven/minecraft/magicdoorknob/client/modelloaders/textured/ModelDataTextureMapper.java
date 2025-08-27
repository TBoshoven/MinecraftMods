package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.TextureSourceReference;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Extract the texture location from extra model data.
 */
class ModelDataTextureMapper implements TextureMapper {
    @Override
    public BlockStateTextureMapper forBlockState(@Nullable BlockState blockState, @Nullable ModelData modelData) {
        if (modelData == null) {
            // We'll never map anything
            return new BlockStateTextureMapper.Empty();
        }

        Map<ModelTextureProperty, TextureSourceReference> lookup = distill(modelData);
        return new BlockStateTextureMapper() {
            @Override
            public Object getMappingKey() {
                // The lookup itself is already a precise implementation of a key
                return lookup;
            }

            @Override
            public @Nullable TextureSourceReference mapSprite(PropertySprite spriteToMap) {
                ResourceLocation name = spriteToMap.contents().name();
                ModelProperty<TextureSourceReference> modelProperty = ModelTextureProperty.get(name);
                return lookup.get(modelProperty);
            }
        };
    }

    /**
     * Distill incoming model data into a compact map with only the properties that might be relevant to the texture
     * mapper.
     *
     * @param modelData The model data to distill down.
     * @return A map containing all the potentially relevant properties and their values.
     */
    private static Map<ModelTextureProperty, TextureSourceReference> distill(ModelData modelData) {
        Map<ModelTextureProperty, TextureSourceReference> result = new Reference2ObjectArrayMap<>();
        for (ModelProperty<?> property : modelData.getProperties()) {
            if (property instanceof ModelTextureProperty modelTextureProperty) {
                TextureSourceReference value = modelData.get(modelTextureProperty);
                if (value != null) {
                    result.put(modelTextureProperty, value);
                }
            }
        }
        // In the unlikely case that we have more than a few texture properties in the model data, hash maps are faster
        if (result.size() > 4) {
            return new Reference2ObjectOpenHashMap<>(result);
        }
        return result;
    }
}
