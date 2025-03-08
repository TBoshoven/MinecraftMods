package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.GeometryLoaderManager;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Set;

/**
 * A model loader that deals with dynamic properties that are used to determine textures at runtime.
 */
public class TexturedGeometryLoader implements IGeometryLoader<TexturedUnbakedGeometry> {
    // All extra textures that were requested.
    private final Set<Material> extraTextures = Sets.newHashSet();

    /**
     * Register an additional texture to load with the models.
     * This makes the texture available for use in properties.
     *
     * @param material The material to include
     */
    public void registerTexture(Material material) {
        extraTextures.add(material);
    }

    @Override
    public TexturedUnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        // Load the original model through its configured base loader
        JsonElement baseLoaderLocation = jsonObject.get("base_loader");
        ResourceLocation baseLoaderName = baseLoaderLocation == null ?  new ResourceLocation("forge", "elements") : new ResourceLocation(baseLoaderLocation.getAsString());
        IGeometryLoader<?> baseLoader = GeometryLoaderManager.get(baseLoaderName);
        if (baseLoader == null) {
            throw new RuntimeException(String.format("Invalid base loader \"%s\"", baseLoaderName));
        }
        IUnbakedGeometry<?> modelGeometry = baseLoader.read(jsonObject, deserializationContext);
        return new TexturedUnbakedGeometry(modelGeometry, extraTextures);
    }
}
