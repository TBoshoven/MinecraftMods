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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
@SideOnly(Side.CLIENT)
class TexturedModel implements IModel {
    // The namespace of the properties; used in model definitions
    private static final String PROPERTY_NAMESPACE = "property";
    // The extra textures to include with this model; used to enable textures that are not already present in the game
    private final Set<ResourceLocation> extraTextures;
    // The original model
    private IModel wrappedModel;

    /**
     * @param wrappedModel  The original model
     * @param extraTextures The extra textures to include with this model; used to enable textures that are not already
     *                      present in the game
     */
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
        // Filter out the property textures since they don't get filled in until runtime
        Set<ResourceLocation> textures = wrappedModel.getTextures().stream()
                .filter(location -> !PROPERTY_NAMESPACE.equals(location.getResourceDomain()))
                .collect(Collectors.toSet());
        return Sets.union(textures, extraTextures);
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        // Use a custom texture getter and baked model
        Function<ResourceLocation, TextureAtlasSprite> augmentedTextureGetter = resourceLocation -> {
            if (PROPERTY_NAMESPACE.equals(resourceLocation.getResourceDomain())) {
                return new PropertySprite(resourceLocation.getResourcePath());
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
