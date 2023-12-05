package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;


import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
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
     * @param name The location of the property
     */
    PropertySprite(ResourceLocation name) {
        super(name, 1, 1);
        // Set the UV values, to make sure they're not both 0.
        // Something is very wonky in the logic for "makeBakedQuad" but these values seem to work when used with
        // BakedQuadRetextured.
        init(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
    }
}
