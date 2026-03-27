package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.MaterialInfoSource;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelMaterialInfoProperty;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jspecify.annotations.Nullable;

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

        Map<ModelMaterialInfoProperty, MaterialInfoSource> lookup = distill(modelData);
        return new BlockStateTextureMapper() {
            @Override
            public Object getMappingKey() {
                // The lookup itself is already a precise implementation of a key
                return lookup;
            }

            @Override
            public @Nullable MaterialInfoSource mapSprite(PropertySprite spriteToMap) {
                Identifier name = spriteToMap.contents().name();
                ModelProperty<MaterialInfoSource> modelProperty = ModelMaterialInfoProperty.get(name);
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
    private static Map<ModelMaterialInfoProperty, MaterialInfoSource> distill(ModelData modelData) {
        Map<ModelMaterialInfoProperty, MaterialInfoSource> result = new Reference2ObjectArrayMap<>();
        for (ModelProperty<?> property : modelData.getProperties()) {
            if (property instanceof ModelMaterialInfoProperty modelMaterialInfoProperty) {
                MaterialInfoSource value = modelData.get(modelMaterialInfoProperty);
                if (value != null) {
                    result.put(modelMaterialInfoProperty, value);
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
