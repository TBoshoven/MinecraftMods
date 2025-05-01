package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedGeometry;
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
    public @Nullable UnbakedGeometry geometry() {
        UnbakedGeometry originalGeometry = delegate.geometry();
        if (originalGeometry == null) {
            return null;
        }
        return new UnbakedGeometry() {
            @Override
            public QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, ModelState modelState, ModelDebugName debugName) {
                //noinspection deprecation
                return originalGeometry.bake(textureSlots, augment(baker), modelState, debugName);
            }

            @Override
            public QuadCollection bake(TextureSlots textureSlots, ModelBaker baker, ModelState state, ModelDebugName debugName, ContextMap additionalProperties) {
                return originalGeometry.bake(textureSlots, augment(baker), state, debugName, additionalProperties);
            }
        };
    }

    /**
     * Augment a model baker with the mapped textures.
     *
     * @param modelBaker The model baker to enhance with texturing functionality.
     * @return The augmented model baker.
     */
    private static ModelBaker augment(ModelBaker modelBaker) {
        return new ModelBaker() {
            @Override
            public SpriteGetter sprites() {
                return new SpriteGetter() {
                    @Override
                    public TextureAtlasSprite get(Material material, ModelDebugName debugName) {
                        if (ModelTextureProperty.PROPERTY_NAMESPACE.equals(material.texture().getNamespace())) {
                            return new PropertySprite(material.texture());
                        }
                        return modelBaker.sprites().get(material, debugName);
                    }

                    @Override
                    public TextureAtlasSprite reportMissingReference(String reference, ModelDebugName debugName) {
                        return modelBaker.sprites().reportMissingReference(reference, debugName);
                    }
                };
            }

            @Override
            public <T> T compute(SharedOperationKey<T> key) {
                return modelBaker.compute(key);
            }

            @Override
            public ResolvedModel getModel(ResourceLocation modelLocation) {
                return modelBaker.getModel(modelLocation);
            }

            @Override
            public ResolvedModel resolveInlineModel(UnbakedModel inlineModel, ModelDebugName debugName) {
                return modelBaker.resolveInlineModel(inlineModel, debugName);
            }
        };
    }
}
