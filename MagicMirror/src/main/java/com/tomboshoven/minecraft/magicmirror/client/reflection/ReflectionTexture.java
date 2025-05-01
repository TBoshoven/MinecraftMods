package com.tomboshoven.minecraft.magicmirror.client.reflection;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.tomboshoven.minecraft.magicmirror.mixin.MinecraftRenderTargetMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;

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
        renderTarget = new TextureTarget(String.format("reflection%d", textureCount++), width, height, true);
        this.texture = renderTarget.getColorTexture();
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
            commandEncoder.clearDepthTexture(depthTexture, 1.0);
        }
        GpuTexture colorTexture = renderTarget.getColorTexture();
        if (colorTexture != null) {
            commandEncoder.clearColorTexture(colorTexture, 0);
        }
    }

    /**
     * Make the reflection texture the main render target.
     * This changes the target for most render states.
     *
     * @return The old main render target, to be used with unbindWriteAsMain\.
     */
    RenderTarget activate() {
        // We use a mixin to temporarily replace it.
        MinecraftRenderTargetMixin minecraftRenderTargetMixin = (MinecraftRenderTargetMixin) Minecraft.getInstance();
        RenderTarget oldMainRenderTarget = minecraftRenderTargetMixin.getMainRenderTarget();
        minecraftRenderTargetMixin.setMainRenderTarget(renderTarget);
        return oldMainRenderTarget;
    }

    /**
     * Restore the old main render target.
     *
     * @param oldMainRenderTarget The old main render target, as returned by unbindWriteAsMain.
     */
    void deactivate(RenderTarget oldMainRenderTarget) {
        MinecraftRenderTargetMixin minecraftRenderTargetMixin = (MinecraftRenderTargetMixin) Minecraft.getInstance();
        minecraftRenderTargetMixin.setMainRenderTarget(oldMainRenderTarget);
    }
}
