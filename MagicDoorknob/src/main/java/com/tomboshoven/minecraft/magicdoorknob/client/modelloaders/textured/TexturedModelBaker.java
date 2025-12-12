package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.resources.Identifier;

/**
 * Implementation of a model baker that makes sure the property texture namespace is mapped appropriately.
 */
public class TexturedModelBaker implements ModelBaker {
    // The original model baker on which our implementation is based.
    private final ModelBaker baseBaker;
    private final Identifier supportedAtlas;

    public TexturedModelBaker(ModelBaker baseBaker, Identifier supportedAtlas) {
        this.baseBaker = baseBaker;
        this.supportedAtlas = supportedAtlas;
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
    public BlockModelPart missingBlockModelPart() {
        return baseBaker.missingBlockModelPart();
    }

    @Override
    public PartCache parts() {
        return baseBaker.parts();
    }

    @Override
    public ResolvedModel getModel(Identifier identifier) {
        return baseBaker.getModel(identifier);
    }

    @Override
    public SpriteGetter sprites() {
        return new TexturedSpriteGetter(baseBaker.sprites(), supportedAtlas);
    }
}
