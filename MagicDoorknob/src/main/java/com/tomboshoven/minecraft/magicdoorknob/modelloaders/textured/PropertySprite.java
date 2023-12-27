package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;


import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A sprite that does not link to a texture.
 * Instead, it describes a property that allows others to provide the texture (see ITextureMapper).
 */
@OnlyIn(Dist.CLIENT)
public class PropertySprite extends TextureAtlasSprite {
    private static final AtlasTexture ATLAS_TEXTURE = new AtlasTexture(new ResourceLocation(MagicDoorknobMod.MOD_ID, "property_texture_atlas"));
    private static final NativeImage NATIVE_IMAGE = new NativeImage(0, 0, false);

    /**
     * @param name The location of the property
     */
    PropertySprite(ResourceLocation name) {
        super(ATLAS_TEXTURE, new Info(name, Integer.MAX_VALUE, Integer.MAX_VALUE, AnimationMetadataSection.EMPTY), 0, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0, NATIVE_IMAGE);
    }

    public String toString() {
        return "PropertySprite{name='" + getName() + "'}";
    }
}
