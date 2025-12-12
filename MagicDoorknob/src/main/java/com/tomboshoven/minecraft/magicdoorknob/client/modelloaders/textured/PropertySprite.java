package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.textures.UnitTextureAtlasSprite;

/**
 * A sprite that does not link to a texture.
 * Instead, it describes a property that allows others to provide the texture (see ITextureMapper).
 */
public class PropertySprite extends TextureAtlasSprite {
    private final Identifier property;

    /**
     * @param property        The identifier of the property.
     * @param compatibleAtlas A location of a compatible atlas.
     */
    PropertySprite(Identifier property, Identifier compatibleAtlas) {
        // Hijack the native image from UnitTextureAtlasSprite, so we don't have to build our own.
        super(compatibleAtlas, new SpriteContents(property, new FrameSize(1, 1), UnitTextureAtlasSprite.INSTANCE.contents().getOriginalImage()), 1, 1, 0, 0, 0);
        this.property = property;
    }

    public Identifier getProperty() {
        return property;
    }

    public String toString() {
        return "PropertySprite{name='%s'}".formatted(property);
    }
}
