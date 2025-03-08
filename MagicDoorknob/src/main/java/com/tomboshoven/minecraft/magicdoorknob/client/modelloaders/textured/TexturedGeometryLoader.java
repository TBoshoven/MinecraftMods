package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Set;

/**
 * A model loader that deals with dynamic properties that are used to determine textures at runtime.
 */
public class TexturedGeometryLoader implements IModelLoader<TexturedUnbakedGeometry> {
    // All extra textures that were requested.
    private final Set<Material> extraTextures = Sets.newHashSet();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

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
    public TexturedUnbakedGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        // Load the original model through its configured base loader
        JsonElement baseLoaderLocation = modelContents.get("base_loader");
        ResourceLocation baseLoaderName = baseLoaderLocation == null ?  new ResourceLocation("elements") : new ResourceLocation(baseLoaderLocation.getAsString());
        IModelGeometry<?> modelGeometry = ModelLoaderRegistry.getModel(baseLoaderName, deserializationContext, modelContents);
        return new TexturedUnbakedGeometry(modelGeometry, extraTextures);
    }
}
