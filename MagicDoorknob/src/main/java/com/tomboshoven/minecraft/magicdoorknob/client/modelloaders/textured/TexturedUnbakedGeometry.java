package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.google.common.collect.Sets;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty.PROPERTY_NAMESPACE;

/**
 * A model that has dynamic properties that are used to determine textures at runtime.
 */
public class TexturedUnbakedGeometry implements IModelGeometry<TexturedUnbakedGeometry> {
    // The extra textures to include with this model; used to enable textures that are not already present in the game
    private final Set<? extends ResourceLocation> extraTextures;
    // The original model
    private final IModelGeometry<?> originalModelGeometry;

    /**
     * @param originalModelGeometry The original model geometry
     * @param extraTextures         The extra textures to include with this model; used to enable textures that are not
     *                              already present in the game
     */
    TexturedUnbakedGeometry(IModelGeometry<?> originalModelGeometry, Set<? extends ResourceLocation> extraTextures) {
        this.originalModelGeometry = originalModelGeometry;
        this.extraTextures = extraTextures;
    }

    @Override
    public Collection<ResourceLocation> getTextureDependencies(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        // Filter out the property textures since they don't get filled in until runtime
        Set<ResourceLocation> textures = originalModelGeometry.getTextureDependencies(owner, modelGetter, missingTextureErrors).stream()
                .filter(location -> !PROPERTY_NAMESPACE.equals(location.getNamespace()))
                .collect(Collectors.toSet());
        return Sets.union(textures, extraTextures);
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format, ItemOverrideList overrides) {
        // Use a custom texture getter and baked model
        Function<ResourceLocation, TextureAtlasSprite> augmentedSpriteGetter = resourceLocation -> {
            if (PROPERTY_NAMESPACE.equals(resourceLocation.getNamespace())) {
                return new PropertySprite(resourceLocation);
            }
            return spriteGetter.apply(resourceLocation);
        };
        return new TexturedBakedModel<>(originalModelGeometry.bake(owner, bakery, augmentedSpriteGetter, sprite, format, overrides), spriteGetter, new ModelDataTextureMapper());
    }
}
