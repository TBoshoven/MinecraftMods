package com.tomboshoven.minecraft.magicmirror.client.reflection;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.joml.Vector4f;

/**
 * Simple wrapper around a render target to a texture, so we can use it with the texture manager.
 */
class ReflectionTexture extends AbstractTexture {
    private static int textureCount;

    /**
     * The frame buffer which is used for rendering the reflection to and subsequently rendering it into the world.
     */
    private final RenderTarget renderTarget;

    ReflectionTexture(int width, int height) {
        renderTarget = new TextureTarget(String.format("reflection%d", textureCount++), width, height, true, GpuFormat.RGBA8_UNORM);
        texture = renderTarget.getColorTexture();
        textureView = renderTarget.getColorTextureView();
    }

    @Override
    public void close() {
        renderTarget.destroyBuffers();
    }

    /**
     * Clear the texture.
     */
    void clear() {
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        GpuTexture depthTexture = renderTarget.getDepthTexture();
        if (depthTexture != null) {
            commandEncoder.clearDepthTexture(depthTexture, 0);
        }
        GpuTexture colorTexture = renderTarget.getColorTexture();
        if (colorTexture != null) {
            commandEncoder.clearColorTexture(colorTexture, new Vector4f(0, 0, 0, 0));
        }
    }

    /**
     * Make the reflection texture the main render target.
     */
    void activate() {
        RenderSystem.outputColorTextureOverride = textureView;
        RenderSystem.outputDepthTextureOverride = renderTarget.getDepthTextureView();
    }

    /**
     * Restore the old main render target.
     */
    void deactivate() {
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
    }
}
