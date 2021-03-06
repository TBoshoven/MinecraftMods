package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedModelLoader.PROPERTY_NAMESPACE;

/**
 * A model that has dynamic properties that are used to determine textures at runtime.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
class TexturedModelGeometry implements IModelGeometry<TexturedModelGeometry> {
    // The extra textures to include with this model; used to enable textures that are not already present in the game
    private final Set<? extends RenderMaterial> extraTextures;
    // The original model
    private final IModelGeometry<?> originalModelGeometry;

    /**
     * @param originalModelGeometry The original model geometry
     * @param extraTextures         The extra textures to include with this model; used to enable textures that are not
     *                              already present in the game
     */
    TexturedModelGeometry(IModelGeometry<?> originalModelGeometry, Set<? extends RenderMaterial> extraTextures) {
        this.originalModelGeometry = originalModelGeometry;
        this.extraTextures = extraTextures;
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        // Filter out the property textures since they don't get filled in until runtime
        Set<RenderMaterial> textures = originalModelGeometry.getTextures(owner, modelGetter, missingTextureErrors).stream()
                .filter(location -> !PROPERTY_NAMESPACE.equals(location.getTextureLocation().getNamespace()))
                .collect(Collectors.toSet());
        return Sets.union(textures, extraTextures);
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        // Use a custom texture getter and baked model
        Function<RenderMaterial, TextureAtlasSprite> augmentedSpriteGetter = material -> {
            if (PROPERTY_NAMESPACE.equals(material.getTextureLocation().getNamespace())) {
                return new PropertySprite(material.getTextureLocation());
            }
            return spriteGetter.apply(material);
        };
        return new TexturedBakedModel<>(originalModelGeometry.bake(owner, bakery, augmentedSpriteGetter, modelTransform, overrides, modelLocation), spriteGetter, new ModelDataTextureMapper());
    }
}
