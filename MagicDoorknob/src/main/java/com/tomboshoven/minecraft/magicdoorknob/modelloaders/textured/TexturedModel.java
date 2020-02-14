package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.IClip;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class TexturedModel implements IModel {
    private static final String PROPERTY_NAMESPACE = "property";
    private final Set<ResourceLocation> extraTextures;
    private IModel wrappedModel;

    TexturedModel(IModel wrappedModel, Set<ResourceLocation> extraTextures) {
        this.wrappedModel = wrappedModel;
        this.extraTextures = extraTextures;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return wrappedModel.getDependencies();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        Set<ResourceLocation> textures = wrappedModel.getTextures().stream()
                .filter(location -> !PROPERTY_NAMESPACE.equals(location.getNamespace()))
                .collect(Collectors.toSet());
        return Sets.union(textures, extraTextures);
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        Function<ResourceLocation, TextureAtlasSprite> augmentedTextureGetter = resourceLocation -> {
            if (PROPERTY_NAMESPACE.equals(resourceLocation.getNamespace())) {
                return new PropertySprite(resourceLocation.getPath());
            }
            return bakedTextureGetter.apply(resourceLocation);
        };
        return new TexturedBakedModel(wrappedModel.bake(state, format, augmentedTextureGetter), bakedTextureGetter, new BlockStateTextureMapper());
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
    public IModel process(ImmutableMap<String, String> customData) {
        return new TexturedModel(wrappedModel.process(customData), extraTextures);
    }

    @Override
    public IModel smoothLighting(boolean value) {
        return new TexturedModel(wrappedModel.smoothLighting(value), extraTextures);
    }

    @Override
    public IModel gui3d(boolean value) {
        return new TexturedModel(wrappedModel.gui3d(value), extraTextures);
    }

    @Override
    public IModel uvlock(boolean value) {
        return new TexturedModel(wrappedModel.uvlock(value), extraTextures);
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        return new TexturedModel(wrappedModel.retexture(textures), extraTextures);
    }

    @Override
    public Optional<ModelBlock> asVanillaModel() {
        return wrappedModel.asVanillaModel();
    }
}
