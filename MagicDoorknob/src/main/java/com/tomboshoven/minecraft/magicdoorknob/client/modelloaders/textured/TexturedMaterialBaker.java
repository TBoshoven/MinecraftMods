package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelMaterialInfoProperty;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * A material baker that wraps an existing material baker, but performs property lookups.
 */
public class TexturedMaterialBaker extends MaterialBaker {
    // The underlying material baker
    private final MaterialBaker baseMaterialBaker;
    // The atlas to use when generating property sprites
    private final Identifier supportedAtlas;

    /**
     * @param baseMaterialBaker The material baker to wrap
     * @param supportedAtlas    The atlas to use when generating property sprites
     */
    public TexturedMaterialBaker(MaterialBaker baseMaterialBaker, Identifier supportedAtlas) {
        // Arbitrary atlas; we don't actually bake materials ourselves, so atlas lookups are only done for the "missing"
        // texture.
        SpriteLoader.Preparations atlas = new SpriteLoader.Preparations(0, 0, 0, baseMaterialBaker.get(new Material(MissingTextureAtlasSprite.getLocation()), () -> "missing").sprite(), Map.of(), CompletableFuture.completedFuture(null));
        super(atlas, atlas);
        this.baseMaterialBaker = baseMaterialBaker;
        this.supportedAtlas = supportedAtlas;
    }

    @Override
    public Material.Baked get(Material material, ModelDebugName debugName) {
        if (ModelMaterialInfoProperty.PROPERTY_NAMESPACE.equals(material.sprite().getNamespace())) {
            return new Material.Baked(new PropertySprite(material.sprite(), supportedAtlas), false);
        }
        return baseMaterialBaker.get(material, debugName);
    }

    @Override
    public Material.Baked reportMissingReference(String reference, ModelDebugName debugName) {
        return baseMaterialBaker.reportMissingReference(reference, debugName);
    }
}
