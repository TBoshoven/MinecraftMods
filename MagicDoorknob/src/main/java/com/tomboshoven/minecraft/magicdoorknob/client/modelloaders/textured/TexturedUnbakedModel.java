package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.client.model.DelegateUnbakedModel;
import org.jetbrains.annotations.Nullable;

/**
 * A model that has dynamic properties that are used to determine textures at runtime.
 */
public class TexturedUnbakedModel extends DelegateUnbakedModel {
    /**
     * @param originalUnbakedModel The original unbaked Vanilla model
     */
    TexturedUnbakedModel(UnbakedModel originalUnbakedModel) {
        super(originalUnbakedModel);
    }

    @Override
    public BakedModel bake(TextureSlots textures, ModelBaker baker, ModelState modelState, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms itemTransforms, ContextMap additionalProperties) {
        return new TexturedBakedModel(wrapped.bake(textures, augment(baker), modelState, useAmbientOcclusion, usesBlockLight, itemTransforms, additionalProperties), baker.sprites(), new ModelDataTextureMapper());
    }

    /**
     * Augment a model baker with the mapped textures.
     */
    private static ModelBaker augment(ModelBaker modelBaker) {
        return new ModelBaker() {
            @Override
            public BakedModel bake(ResourceLocation location, ModelState transform) {
                return modelBaker.bake(location, transform);
            }

            @Override
            public SpriteGetter sprites() {
                return new SpriteGetter() {
                    @Override
                    public TextureAtlasSprite get(Material material) {
                        if (ModelTextureProperty.PROPERTY_NAMESPACE.equals(material.texture().getNamespace())) {
                            return new PropertySprite(material.texture());
                        }
                        return modelBaker.sprites().get(material);
                    }

                    @Override
                    public TextureAtlasSprite reportMissingReference(String reference) {
                        return modelBaker.sprites().reportMissingReference(reference);
                    }
                };
            }

            @Override
            public ModelDebugName rootName() {
                return modelBaker.rootName();
            }

            @Override
            public @Nullable UnbakedModel getModel(ResourceLocation location) {
                return modelBaker.getModel(location);
            }
        };
    }
}
