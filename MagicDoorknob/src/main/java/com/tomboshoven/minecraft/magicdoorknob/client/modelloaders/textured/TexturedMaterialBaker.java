package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelMaterialInfoProperty;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;

/**
 * A material baker that wraps an existing material baker, but performs property lookups.
 */
public class TexturedMaterialBaker implements MaterialBaker {
    // The underlying material baker
    private final MaterialBaker baseMaterialBaker;
    // The atlas to use when generating property sprites
    private final Identifier supportedAtlas;

    /**
     * @param baseMaterialBaker The material baker to wrap
     * @param supportedAtlas    The atlas to use when generating property sprites
     */
    public TexturedMaterialBaker(MaterialBaker baseMaterialBaker, Identifier supportedAtlas) {
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
