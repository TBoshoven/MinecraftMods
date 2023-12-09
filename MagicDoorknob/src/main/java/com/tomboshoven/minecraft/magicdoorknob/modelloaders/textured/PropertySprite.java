package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A sprite that does not link to a texture.
 * Instead, it describes a property that allows others to provide the texture (see ITextureMapper).
 */
@SideOnly(Side.CLIENT)
public class PropertySprite extends TextureAtlasSprite {
    /**
     * @param name The name of the property
     */
    PropertySprite(String name) {
        super(name);
        width = 16;
        height = 16;
        initSprite(16, 16, 0, 0, false);
    }
}
