package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;


import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.textures.UnitTextureAtlasSprite;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A sprite that does not link to a texture.
 * Instead, it describes a property that allows others to provide the texture (see ITextureMapper).
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class PropertySprite extends TextureAtlasSprite {
    private final ResourceLocation property;

    /**
     * @param property The identifier of the property
     */
    PropertySprite(ResourceLocation property) {
        // Hijack the atlas and native image from UnitTextureAtlasSprite, so we don't have to build our own.
        super(UnitTextureAtlasSprite.LOCATION, new SpriteContents(property, new FrameSize(1, 1), UnitTextureAtlasSprite.INSTANCE.contents().getOriginalImage(), ResourceMetadata.EMPTY), 1, 1, 0, 0);
        this.property = property;
    }

    public ResourceLocation getProperty() {
        return this.property;
    }

    @Override
    public float uvShrinkRatio() {
        // The default calculated value for this seriously explodes for tiny textures
        // We should be fine without; if needed, we can apply this during re-texturing
        return 0;
    }

    public String toString() {
        return "PropertySprite{name='" + property + "'}";
    }
}
