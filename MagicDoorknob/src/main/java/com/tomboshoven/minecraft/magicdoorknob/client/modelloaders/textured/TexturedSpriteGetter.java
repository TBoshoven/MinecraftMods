package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.ModelTextureProperty;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.SpriteGetter;

public class TexturedSpriteGetter implements SpriteGetter {
    private final SpriteGetter baseSpriteGetter;

    public TexturedSpriteGetter(SpriteGetter baseSpriteGetter) {
        this.baseSpriteGetter = baseSpriteGetter;
    }

    @Override
    public TextureAtlasSprite get(Material material, ModelDebugName debugName) {
        if (ModelTextureProperty.PROPERTY_NAMESPACE.equals(material.texture().getNamespace())) {
            return new PropertySprite(material.texture());
        }
        return baseSpriteGetter.get(material, debugName);
    }

    @Override
    public TextureAtlasSprite reportMissingReference(String reference, ModelDebugName debugName) {
        return baseSpriteGetter.reportMissingReference(reference, debugName);
    }
}
