package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A model that has dynamic properties that are used to determine textures at runtime.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
class TexturedModel implements IUnbakedModel {
    // The namespace of the properties; used in model definitions
    private static final String PROPERTY_NAMESPACE = "property";
    // The extra textures to include with this model; used to enable textures that are not already present in the game
    private final Set<ResourceLocation> extraTextures;
    // The original model
    private IUnbakedModel wrappedModel;

    /**
     * @param wrappedModel  The original model
     * @param extraTextures The extra textures to include with this model; used to enable textures that are not already
     *                      present in the game
     */
    TexturedModel(IUnbakedModel wrappedModel, Set<ResourceLocation> extraTextures) {
        this.wrappedModel = wrappedModel;
        this.extraTextures = extraTextures;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return wrappedModel.getDependencies();
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        // Filter out the property textures since they don't get filled in until runtime
        Set<ResourceLocation> textures = wrappedModel.getTextures(modelGetter, missingTextureErrors).stream()
                .filter(location -> !PROPERTY_NAMESPACE.equals(location.getNamespace()))
                .collect(Collectors.toSet());
        return Sets.union(textures, extraTextures);
    }

    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
        // Use a custom texture getter and baked model
        Function<ResourceLocation, TextureAtlasSprite> augmentedSpriteGetter = resourceLocation -> {
            if (PROPERTY_NAMESPACE.equals(resourceLocation.getNamespace())) {
                return new PropertySprite(resourceLocation.getPath());
            }
            return spriteGetter.apply(resourceLocation);
        };
        return new TexturedBakedModel(wrappedModel.bake(bakery, augmentedSpriteGetter, sprite, format), spriteGetter, new ModelDataTextureMapper());
    }

    @Override
    public IModelState getDefaultState() {
        return wrappedModel.getDefaultState();
    }

    @Override
    public Optional<? extends IClip> getClip(String name) {
        return wrappedModel.getClip(name);
    }

    @Override
    public IUnbakedModel process(ImmutableMap<String, String> customData) {
        return new TexturedModel(wrappedModel.process(customData), extraTextures);
    }

    @Override
    public IUnbakedModel smoothLighting(boolean value) {
        return new TexturedModel(wrappedModel.smoothLighting(value), extraTextures);
    }

    @Override
    public IUnbakedModel gui3d(boolean value) {
        return new TexturedModel(wrappedModel.gui3d(value), extraTextures);
    }

    @Override
    public IUnbakedModel retexture(ImmutableMap<String, String> textures) {
        return new TexturedModel(wrappedModel.retexture(textures), extraTextures);
    }
}
