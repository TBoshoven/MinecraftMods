package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry2;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

/**
 * A model loader that deals with dynamic properties that are used to determine textures at runtime.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class TexturedModelLoader implements IModelLoader<TexturedModelGeometry> {
    // The namespace of the properties; used in model definitions
    public static final String PROPERTY_NAMESPACE = "property";
    // All extra textures that were requested.
    private final Set<ResourceLocation> extraTextures = Sets.newHashSet();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    /**
     * Register an additional texture to load with the models.
     * This makes the texture available for use in properties.
     *
     * @param textureLocation The location of the texture to include
     */
    public void registerTexture(ResourceLocation textureLocation) {
        extraTextures.add(textureLocation);
    }

    @Override
    public TexturedModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        // Load the original model through its configured base loader
        ResourceLocation baseLoader = new ResourceLocation(modelContents.get("base_loader").getAsString());
        IModelGeometry<?> modelGeometry = ModelLoaderRegistry2.getModel(baseLoader, deserializationContext, modelContents);
        return new TexturedModelGeometry(modelGeometry, extraTextures);
    }
}
