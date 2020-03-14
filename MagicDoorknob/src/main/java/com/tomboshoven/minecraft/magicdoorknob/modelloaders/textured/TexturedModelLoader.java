package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A model loader that deals with dynamic properties that are used to determine textures at runtime.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class TexturedModelLoader implements ICustomModelLoader {
    // The namespace of the properties; used in model definitions
    public static final String PROPERTY_NAMESPACE = "property";
    // A mapping from configured textured models to the models they're based on
    private Map<ResourceLocation, ResourceLocation> baseModels = Maps.newHashMap();
    // All extra textures that were requested.
    private Set<ResourceLocation> extraTextures = Sets.newHashSet();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        // Check to see if we know about the given model
        return baseModels.containsKey(new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath()));
    }

    /**
     * Register a new textured model.
     *
     * @param modelLocation     The location for the textured model
     * @param baseModelLocation The location of the model to base it on
     */
    public void register(ResourceLocation modelLocation, ResourceLocation baseModelLocation) {
        ResourceLocation actualLocation = ModelLoaderRegistry.getActualLocation(modelLocation);
        baseModels.put(actualLocation, baseModelLocation);
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
    public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
        ResourceLocation baseModelLocation = baseModels.getOrDefault(modelLocation, ModelBakery.MODEL_MISSING);
        // FIXME: This causes the untextured model to be cached and dummy textures to be requested
        IUnbakedModel baseModel = ModelLoaderRegistry.getModel(baseModelLocation);
        return new TexturedModel(baseModel, Collections.unmodifiableSet(extraTextures));
    }
}
