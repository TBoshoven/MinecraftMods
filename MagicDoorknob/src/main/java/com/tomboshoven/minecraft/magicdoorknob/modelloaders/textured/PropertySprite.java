package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;


import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A sprite that does not link to a texture.
 * Instead, it describes a property that allows others to provide the texture (see ITextureMapper).
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
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
