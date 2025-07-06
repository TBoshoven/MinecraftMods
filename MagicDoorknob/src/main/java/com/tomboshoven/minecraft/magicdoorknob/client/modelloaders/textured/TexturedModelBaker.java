package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.resources.ResourceLocation;

/**
 * Implementation of a model baker that makes sure the property texture namespace is mapped appropriately.
 */
public class TexturedModelBaker implements ModelBaker {
    // The original model baker on which our implementation is based.
    private final ModelBaker baseBaker;

    public TexturedModelBaker(ModelBaker baseBaker) {
        this.baseBaker = baseBaker;
    }

    @Override
    public <T> T compute(ModelBaker.SharedOperationKey<T> key) {
        // Perform the original computation, but instead of injecting the base baker, inject ourselves.
        return baseBaker.compute(baker -> {
            if (baker == baseBaker) {
                return key.compute(TexturedModelBaker.this);
            }
            // A model baker got injected somewhere.
            // This is not supposed to happen, so let's take the safe path.
            return key.compute(baker);
        });
    }

    @Override
    public ResolvedModel getModel(ResourceLocation modelLocation) {
        return baseBaker.getModel(modelLocation);
    }

    @Override
    public SpriteGetter sprites() {
        return new TexturedSpriteGetter(baseBaker.sprites());
    }
}
