package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A model loader that deals with dynamic properties that are used to determine textures at runtime.
 */
@SideOnly(Side.CLIENT)
public class TexturedModelLoader implements ICustomModelLoader {
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
        baseModels.put(modelLocation, baseModelLocation);
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
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        ModelResourceLocation modelResourceLocation = (ModelResourceLocation) modelLocation;
        ResourceLocation baseResourceLocation = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath());
        ResourceLocation baseModelLocation = baseModels.getOrDefault(baseResourceLocation, ModelBakery.MODEL_MISSING);
        ResourceLocation augmentedBaseModelLocation = new ModelResourceLocation(baseModelLocation, modelResourceLocation.getVariant());
        // FIXME: This causes the untextured model to be cached and dummy textures to be requested
        IModel baseModel = ModelLoaderRegistry.getModel(augmentedBaseModelLocation);
        return new TexturedModel(baseModel, Collections.unmodifiableSet(extraTextures));
    }
}
