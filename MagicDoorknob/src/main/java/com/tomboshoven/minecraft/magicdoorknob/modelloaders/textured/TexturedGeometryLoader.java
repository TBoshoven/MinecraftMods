package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.geometry.GeometryLoaderManager;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A model loader that deals with dynamic properties that are used to determine textures at runtime.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class TexturedGeometryLoader implements IGeometryLoader<TexturedUnbakedGeometry> {
    // The namespace of the properties; used in model definitions
    public static final String PROPERTY_NAMESPACE = "property";

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
