package com.tomboshoven.minecraft.magicmirror.reflection;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.client.Minecraft.ON_OSX;

/**
 * Simple wrapper around a render target to a texture, so we can use it with the texture manager.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class ReflectionTexture extends AbstractTexture {
    /**
     * The frame buffer which is used for rendering the reflection to and subsequently rendering it into the world.
     */
    private final RenderTarget renderTarget;

    public ReflectionTexture(int width, int height) {
        renderTarget = new TextureTarget(width, height, true, ON_OSX);
        renderTarget.unbindWrite();
        id = renderTarget.getColorTextureId();
    }

    @Override
    public void load(ResourceManager manager) {
        // Texture is already loaded, so nothing to do here
    }

    @Override
    public void close() {
        renderTarget.destroyBuffers();
        this.releaseId();
    }

    @Override
    public void releaseId() {
        renderTarget.destroyBuffers();
    }

    /**
     * Clear the texture.
     */
    public void clear() {
        renderTarget.clear(ON_OSX);
    }

    /**
     * Bind the render target for writing.
     *
     * @param viewport Whether to update the viewport to the right size.
     */
    public void bindWrite(boolean viewport) {
        renderTarget.bindWrite(viewport);
    }

    /**
     * Unbind the render target for writing.
     */
    public void unbindWrite() {
        renderTarget.unbindWrite();
    }
}
