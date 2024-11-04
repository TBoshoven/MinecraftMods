package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.GeometryLoaderManager;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

/**
 * A model loader that deals with dynamic properties that are used to determine textures at runtime.
 */
public class TexturedGeometryLoader implements IGeometryLoader<TexturedUnbakedGeometry> {
    @Override
    public TexturedUnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        // Load the original model through its configured base loader
        JsonElement baseLoaderName = jsonObject.remove("base_loader");
        if (baseLoaderName != null) {
            jsonObject.add("loader", baseLoaderName);
            IGeometryLoader<?> baseLoader = GeometryLoaderManager.get(ResourceLocation.parse(baseLoaderName.getAsString()));
            if (baseLoader != null) {
                IUnbakedGeometry<?> modelGeometry = baseLoader.read(jsonObject, deserializationContext);
                return new TexturedUnbakedGeometry.Geometry(modelGeometry);
            }
        }

        jsonObject.remove("loader");
        return new TexturedUnbakedGeometry.Model(deserializationContext.deserialize(jsonObject, BlockModel.class));
    }
}
