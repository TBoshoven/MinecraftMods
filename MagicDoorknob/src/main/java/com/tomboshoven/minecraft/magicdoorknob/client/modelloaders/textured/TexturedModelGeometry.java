package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
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
public class TexturedModelGeometry implements IModelGeometry<TexturedModelGeometry> {
    // The extra textures to include with this model; used to enable textures that are not already present in the game
    private final Set<? extends Material> extraTextures;
    // The original model
    private final IModelGeometry<?> originalModelGeometry;

    /**
     * @param originalModelGeometry The original model geometry
     * @param extraTextures         The extra textures to include with this model; used to enable textures that are not
     *                              already present in the game
     */
    TexturedModelGeometry(IModelGeometry<?> originalModelGeometry, Set<? extends Material> extraTextures) {
        this.originalModelGeometry = originalModelGeometry;
        this.extraTextures = extraTextures;
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        // Filter out the property textures since they don't get filled in until runtime
        Set<Material> textures = originalModelGeometry.getTextures(owner, modelGetter, missingTextureErrors).stream()
                .filter(location -> !PROPERTY_NAMESPACE.equals(location.texture().getNamespace()))
                .collect(Collectors.toSet());
        return Sets.union(textures, extraTextures);
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        // Use a custom texture getter and baked model
        Function<Material, TextureAtlasSprite> augmentedSpriteGetter = material -> {
            if (PROPERTY_NAMESPACE.equals(material.texture().getNamespace())) {
                return new PropertySprite(material.texture());
            }
            return spriteGetter.apply(material);
        };
        return new TexturedBakedModel<>(originalModelGeometry.bake(owner, bakery, augmentedSpriteGetter, modelTransform, overrides, modelLocation), spriteGetter, new ModelDataTextureMapper());
    }
}
