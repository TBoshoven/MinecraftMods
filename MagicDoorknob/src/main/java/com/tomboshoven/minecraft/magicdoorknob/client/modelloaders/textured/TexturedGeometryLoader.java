package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
        ResourceLocation baseLoaderName = new ResourceLocation(jsonObject.get("base_loader").getAsString());
        IGeometryLoader<?> baseLoader = GeometryLoaderManager.get(baseLoaderName);
        if (baseLoader == null) {
            throw new RuntimeException(String.format("Invalid base loader \"%s\"", baseLoaderName));
        }
        IUnbakedGeometry<?> modelGeometry = baseLoader.read(jsonObject, deserializationContext);
        return new TexturedUnbakedGeometry(modelGeometry);
    }
}
