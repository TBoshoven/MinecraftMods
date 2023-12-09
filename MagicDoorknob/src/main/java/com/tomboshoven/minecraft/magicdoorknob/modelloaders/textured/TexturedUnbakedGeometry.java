package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

import static com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedGeometryLoader.PROPERTY_NAMESPACE;

/**
 * A model that has dynamic properties that are used to determine textures at runtime.
 */
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
