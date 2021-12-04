package com.tomboshoven.minecraft.magicmirror.reflection;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;

/**
 * Render type for rendering the reflection texture to the screen.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
class ReflectionRenderType extends RenderType {
    ReflectionRenderType(ReflectionClient reflection) {
        // Builtin RenderType builders are incredibly annoying to use due to how they're set up, so we just roll our own
        super(String.format("reflection[%d]", reflection.hashCode()), POSITION_COLOR_TEX, GL_QUADS, 64, true, true, () -> {
            // Texture
            RenderSystem.enableTexture();

            // Transparency
            RenderSystem.enableBlend();

            if(reflection.bind()) {
                RenderSystem.defaultBlendFunc();
            }
            else {
                // If we couldn't bind the texture, just render nothing
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            }

            // Shade model (TODO: do we need this?)
            RenderSystem.shadeModel(GL_SMOOTH);

            // Alpha
            RenderSystem.disableAlphaTest();

            // Depth test
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL_LEQUAL);

            // Fog
            FogRenderer.levelFogColor();
            RenderSystem.enableFog();
        }, () -> {
            // Fog
            RenderSystem.disableFog();

            // Depth test
            RenderSystem.disableDepthTest();
            RenderSystem.depthFunc(GL_LEQUAL);

            // Alpha
            RenderSystem.disableAlphaTest();
            RenderSystem.defaultAlphaFunc();

            // Shade model
            RenderSystem.shadeModel(GL_FLAT);

            // Transparency
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });
    }
}
