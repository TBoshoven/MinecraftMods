package com.tomboshoven.minecraft.magicmirror.reflection;

import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRenderer;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Client-side version of the reflection.
 * This version actually renders a reflection.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class ReflectionClient extends Reflection {
    /**
     * The renderer for the reflection.
     */
    @Nullable
    private ReflectionRendererBase reflectionRenderer;

    /**
     * The angle (in degrees) of the reflected object.
     */
    private float facing;

    /**
     * The frame buffer which is used for rendering the reflection to and subsequently rendering it into the world.
     */
    @Nullable
    private Framebuffer frameBuffer;

    /**
     * The previous value of partialTicks.
     * Because this value is monotonically increasing between ticks, we can use it to prevent re-rendering the
     * reflection when multiple blocks request it.
     */
    private float lastRenderPartialTicks = -1f;

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

        frameBuffer = new Framebuffer(64, 128, true);
        frameBuffer.unbindFramebuffer();
    }

    @Override
    void cleanUpFrameBuffer() {
        super.cleanUpFrameBuffer();

        if (frameBuffer != null) {
            frameBuffer.deleteFramebuffer();
            frameBuffer = null;
        }
    }

    @Override
    void rebuildRenderer() {
        super.rebuildRenderer();

        Entity reflectedEntity = getReflectedEntity();
        if (reflectedEntity != null) {
            reflectionRenderer = new ReflectionRenderer(reflectedEntity);
            for (ReflectionModifier modifier : modifiers) {
                reflectionRenderer = modifier.apply(reflectionRenderer);
            }
        }
    }

    @Override
    void cleanUpRenderer() {
        reflectionRenderer = null;
    }

    @Override
    public void setFacing(float facing) {
        this.facing = facing;
    }

    @Override
    public void render(float partialTicks) {
        super.render(partialTicks);

        // Don't render twice per partial tick; this is a simple hack for multiblock optimization.
        // This requires that forceRerender() is called each tick.
        if (lastRenderPartialTicks >= partialTicks) {
            return;
        }
        lastRenderPartialTicks = partialTicks;

        if (frameBuffer != null && reflectionRenderer != null) {
            frameBuffer.framebufferClear();
            frameBuffer.bindFramebuffer(true);

            reflectionRenderer.render(facing, partialTicks);

            frameBuffer.unbindFramebuffer();
        }
    }

    @Override
    public void forceRerender() {
        super.forceRerender();

        lastRenderPartialTicks = -1f;
    }

    @Override
    public void bind() {
        super.bind();

        if (frameBuffer == null) {
            throw new RuntimeException("No active reflection");
        }
        frameBuffer.bindFramebufferTexture();
    }

    /**
     * Factory for the reflection, used for creating the proper objects on server-side and client-side.
     * <p>
     * This factory is intended for clients. See Reflection.Factory for the server version.
     */
    @OnlyIn(Dist.CLIENT)
    public static class ReflectionFactory extends Reflection.ReflectionFactory {
        @Override
        public Reflection createClient() {
            return new ReflectionClient();
        }
    }
}
