package com.tomboshoven.minecraft.magicmirror.client.reflection;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.AbstractTexture;

import javax.annotation.Nullable;
import java.util.Objects;

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
        renderTarget = new TextureTarget(String.format("reflection%d", textureCount++), width, height, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
        texture = renderTarget.getColorTexture();
        textureView = renderTarget.getColorTextureView();
    }

    @Override
    public void close() {
        renderTarget.destroyBuffers();
    }

    GpuTextureView colorView() {
        return Objects.requireNonNull(renderTarget.getColorTextureView());
    }

    @Nullable
    GpuTextureView depthView() {
        return renderTarget.getDepthTextureView();
    }
}
