package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

import java.util.List;
import java.util.function.Function;

/**
 * A model that has dynamic properties that are used to determine textures at runtime.
 */
public abstract class TexturedUnbakedGeometry implements IUnbakedGeometry<TexturedUnbakedGeometry> {
    /**
     * Textured unbaked model based on unbaked geometry from a custom model loader.
     */
    public static class Geometry extends TexturedUnbakedGeometry {
        // The original model
        private final IUnbakedGeometry<?> originalModelGeometry;

        /**
         * @param originalModelGeometry The original model geometry
         */
        public Geometry(IUnbakedGeometry<?> originalModelGeometry) {
            this.originalModelGeometry = originalModelGeometry;
        }

        @Override
        public void resolveDependencies(UnbakedModel.Resolver resolver, IGeometryBakingContext context) {
            originalModelGeometry.resolveDependencies(resolver, context);
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, List<ItemOverride> overrides) {
            return new TexturedBakedModel<>(originalModelGeometry.bake(context, baker, augment(spriteGetter), modelTransform, overrides), spriteGetter, new ModelDataTextureMapper());
        }
    }

    /**
     * Textured unbaked model based on an unbaked vanilla model.
     */
    public static class Model extends TexturedUnbakedGeometry {
        // The original model
        private final UnbakedModel originalUnbakedModel;

        /**
         * @param originalUnbakedModel The original unbaked Vanilla model
         */
        public Model(UnbakedModel originalUnbakedModel) {
            this.originalUnbakedModel = originalUnbakedModel;
        }

        @Override
        public void resolveDependencies(UnbakedModel.Resolver resolver, IGeometryBakingContext context) {
            originalUnbakedModel.resolveDependencies(resolver);
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, List<ItemOverride> overrides) {
            return new TexturedBakedModel<>(originalUnbakedModel.bake(baker, augment(spriteGetter), UnbakedGeometryHelper.composeRootTransformIntoModelState(modelTransform, context.getRootTransform())), spriteGetter, new ModelDataTextureMapper());
        }
    }

    /**
     * Augment a sprite getter with the mapped textures.
     */
    protected Function<Material, TextureAtlasSprite> augment(Function<Material, TextureAtlasSprite> spriteGetter) {
        return material -> {
            if (ModelTextureProperty.PROPERTY_NAMESPACE.equals(material.texture().getNamespace())) {
                return new PropertySprite(material.texture());
            }
            return spriteGetter.apply(material);
        };
    }
}
