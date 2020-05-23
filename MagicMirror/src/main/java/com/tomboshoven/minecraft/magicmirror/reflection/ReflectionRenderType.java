package com.tomboshoven.minecraft.magicmirror.reflection;

import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;

/**
 * Render type for rendering the reflection texture to the screen.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class ReflectionRenderType extends RenderType {
    public ReflectionRenderType(ReflectionClient reflection) {
        // Builtin RenderType builders are incredibly annoying to use due to how they're set up, so we just roll our own
        super("reflection", POSITION_COLOR_TEX, GL_QUADS, 64, true, true, () -> {
            // Texture
            RenderSystem.enableTexture();
            reflection.bind();

            // Transparency
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // Shade model (TODO: do we need this?)
            RenderSystem.shadeModel(GL_SMOOTH);

            // Alpha
            RenderSystem.disableAlphaTest();

            // Depth test
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL_LEQUAL);

            // Culling
            RenderSystem.enableCull();

            // Fog
            FogRenderer.applyFog();
            RenderSystem.enableFog();
        }, () -> {
            // Fog
            RenderSystem.disableFog();

            // Culling
            RenderSystem.disableCull();

            // Depth test
            RenderSystem.disableDepthTest();

            // Shade model
            RenderSystem.shadeModel(7424);

            // Transparency
            RenderSystem.disableBlend();
        });
    }
}
