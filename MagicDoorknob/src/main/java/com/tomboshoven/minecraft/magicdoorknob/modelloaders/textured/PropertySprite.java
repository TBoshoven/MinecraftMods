package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;


import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class PropertySprite extends TextureAtlasSprite {
    PropertySprite(String name) {
        super(name);
        width = 16;
        height = 16;
        initSprite(16, 16, 0, 0, false);
    }
}
