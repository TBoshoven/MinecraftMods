package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

import static com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedGeometryLoader.PROPERTY_NAMESPACE;

/**
 * A model that has dynamic properties that are used to determine textures at runtime.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class TexturedUnbakedGeometry implements IUnbakedGeometry<TexturedUnbakedGeometry> {
    // The original model
    private final IUnbakedGeometry<?> originalModelGeometry;

    /**
     * @param originalModelGeometry The original model geometry
     */
    TexturedUnbakedGeometry(IUnbakedGeometry<?> originalModelGeometry) {
        this.originalModelGeometry = originalModelGeometry;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        this.originalModelGeometry.resolveParents(modelGetter, context);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        // Use a custom texture getter and baked model
        Function<Material, TextureAtlasSprite> augmentedSpriteGetter = material -> {
            if (PROPERTY_NAMESPACE.equals(material.texture().getNamespace())) {
                return new PropertySprite(material.texture());
            }
            return spriteGetter.apply(material);
        };
        return new TexturedBakedModel<>(originalModelGeometry.bake(context, baker, augmentedSpriteGetter, modelTransform, overrides, modelLocation), spriteGetter, new ModelDataTextureMapper());
    }
}
