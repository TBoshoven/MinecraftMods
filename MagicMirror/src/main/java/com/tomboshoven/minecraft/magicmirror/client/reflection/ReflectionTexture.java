package com.tomboshoven.minecraft.magicmirror.client.reflection;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.tomboshoven.minecraft.magicmirror.mixin.MinecraftRenderTargetMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;

/**
 * Simple wrapper around a render target to a texture, so we can use it with the texture manager.
 */
class ReflectionTexture extends AbstractTexture {
    /**
     * The frame buffer which is used for rendering the reflection to and subsequently rendering it into the world.
     */
    private final RenderTarget renderTarget;

    ReflectionTexture(int width, int height) {
        renderTarget = new TextureTarget(width, height, true);
        renderTarget.unbindWrite();
        id = renderTarget.getColorTextureId();
    }

    @Override
    public void close() {
        renderTarget.destroyBuffers();
    }

    @Override
    public void releaseId() {
        super.releaseId();
        renderTarget.destroyBuffers();
    }

    /**
     * Clear the texture.
     */
    void clear() {
        renderTarget.clear();
    }

    /**
     * Make the reflection texture the main render target.
     * This changes the target for most render states.
     *
     * @return The old main render target, to be used with unbindWriteAsMain\.
     */
    RenderTarget bindWriteAsMain() {
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
    void unbindWriteAsMain(RenderTarget oldMainRenderTarget) {
        MinecraftRenderTargetMixin minecraftRenderTargetMixin = (MinecraftRenderTargetMixin) Minecraft.getInstance();
        minecraftRenderTargetMixin.setMainRenderTarget(oldMainRenderTarget);
    }
}
