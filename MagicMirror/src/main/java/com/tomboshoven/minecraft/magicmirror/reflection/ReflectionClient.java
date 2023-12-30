package com.tomboshoven.minecraft.magicmirror.reflection;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifiers;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRenderer;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

import static net.minecraft.client.Minecraft.ON_OSX;

/**
 * Client-side version of the reflection.
 * This version actually renders a reflection.
 */
public class ReflectionClient extends Reflection {
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 128;
    /**
     * The renderer for the reflection.
     */
    @Nullable
    private ReflectionRendererBase reflectionRenderer;

    /**
     * The frame buffer which is used for rendering the reflection to and subsequently rendering it into the world.
     */
    @Nullable
    private Framebuffer frameBuffer;

    /**
     * @param blockEntity The block entity corresponding to the mirror that displays the reflection.
     */
    public ReflectionClient(MagicMirrorCoreTileEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    void incrementActiveClientReflections() {
        ++activeReflectionsClient;
    }

    @Override
    void decrementActiveReflections() {
        --activeReflectionsClient;
    }

    @Override
    void buildFrameBuffer() {
        super.buildFrameBuffer();

        frameBuffer = new Framebuffer(TEXTURE_WIDTH, TEXTURE_HEIGHT, true, ON_OSX);
        frameBuffer.unbindWrite();
    }

    @Override
    void cleanUpFrameBuffer() {
        super.cleanUpFrameBuffer();

        if (frameBuffer != null) {
            frameBuffer.destroyBuffers();
            frameBuffer = null;
        }
    }

    @Override
    void rebuildRenderer() {
        super.rebuildRenderer();

        Entity reflectedEntity = getReflectedEntity();
        if (reflectedEntity != null) {
            reflectionRenderer = new ReflectionRenderer(reflectedEntity);
            for (MagicMirrorTileEntityModifier modifier : blockEntity.getModifiers()) {
                ReflectionModifier reflectionModifier = ReflectionModifiers.MODIFIERS.get(modifier.getName());
                if (reflectionModifier != null) {
                    reflectionRenderer = reflectionModifier.apply(modifier, reflectionRenderer);
                }
            }
        }
    }

    @Override
    void cleanUpRenderer() {
        reflectionRenderer = null;
    }

    @Override
    public void render(float partialTicks) {
        super.render(partialTicks);

        // Create or destroy the framebuffer if needed
        if (reflectedEntity != null && frameBuffer == null) {
            buildFrameBuffer();
        } else if (reflectedEntity == null && frameBuffer != null) {
            cleanUpFrameBuffer();
        }

        if (frameBuffer != null && reflectionRenderer != null) {
            frameBuffer.clear(ON_OSX);
            frameBuffer.bindWrite(true);

            reflectionRenderer.setUp();
            reflectionRenderer.render(angle, partialTicks);
            reflectionRenderer.tearDown();

            frameBuffer.unbindWrite();
        }
    }

    @Override
    public void bind() {
        super.bind();

        if (frameBuffer == null) {
            throw new RuntimeException("No active reflection");
        }
        frameBuffer.bindRead();
    }
}
