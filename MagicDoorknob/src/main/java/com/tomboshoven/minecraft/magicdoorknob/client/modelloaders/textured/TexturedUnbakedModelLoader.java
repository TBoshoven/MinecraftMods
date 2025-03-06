package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import net.neoforged.neoforge.client.model.UnbakedModelParser;

/**
 * A model loader that deals with dynamic properties that are used to determine textures at runtime.
 */
public class TexturedUnbakedModelLoader implements UnbakedModelLoader<TexturedUnbakedModel> {
    @Override
    public TexturedUnbakedModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        // Load the original model through its configured base loader
        JsonElement baseLoaderName = jsonObject.remove("base_loader");
        if (baseLoaderName != null) {
            jsonObject.add("loader", baseLoaderName);
            UnbakedModelLoader<?> baseLoader = UnbakedModelParser.get(ResourceLocation.parse(baseLoaderName.getAsString()));
            if (baseLoader != null) {
                UnbakedModel unbakedModel = baseLoader.read(jsonObject, deserializationContext);
                return new TexturedUnbakedModel(unbakedModel);
            }
        }

        jsonObject.remove("loader");
        return new TexturedUnbakedModel(deserializationContext.deserialize(jsonObject, BlockModel.class));
    }
}
