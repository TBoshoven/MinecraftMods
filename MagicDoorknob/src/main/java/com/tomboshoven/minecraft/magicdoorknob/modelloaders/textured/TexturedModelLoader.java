package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TexturedModelLoader implements ICustomModelLoader {
    private Map<ResourceLocation, ResourceLocation> baseModels = Maps.newHashMap();
    private Set<ResourceLocation> extraTextures = Sets.newHashSet();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return baseModels.containsKey(new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath()));
    }

    public void register(ResourceLocation modelLocation, ResourceLocation baseModelLocation) {
        baseModels.put(modelLocation, baseModelLocation);
    }

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
