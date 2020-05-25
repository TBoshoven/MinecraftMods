package com.tomboshoven.minecraft.magicmirror.reflection;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRenderer;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.client.Minecraft.IS_RUNNING_ON_MAC;

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
     * The frame buffer which is used for rendering the reflection to and subsequently rendering it into the world.
     */
    @Nullable
    private Framebuffer frameBuffer;

    /**
     * Render type for rendering this reflection to the screen.
     * Each reflection has its own texture, and therefore its own render type.
     */

    private final RenderType renderType;

    /**
     * The previous value of partialTicks.
     * Because this value is monotonically increasing between ticks, we can use it to prevent re-rendering the
     * reflection when multiple blocks request it.
     */
    private float lastRenderPartialTicks = -1f;

    public ReflectionClient() {
        renderType = new ReflectionRenderType(this);
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

        frameBuffer = new Framebuffer(64, 128, true, IS_RUNNING_ON_MAC);
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
    public void render(float partialTicks) {
        super.render(partialTicks);

        // Create or destroy the framebuffer if needed
        if (reflectedEntity != null && frameBuffer == null) {
            buildFrameBuffer();
        } else if (reflectedEntity == null && frameBuffer != null) {
            cleanUpFrameBuffer();
        }

        // Don't render twice per partial tick; this is a simple hack for multiblock optimization.
        // This requires that forceRerender() is called each tick.
        if (lastRenderPartialTicks >= partialTicks) {
            return;
        }
        lastRenderPartialTicks = partialTicks;

        if (frameBuffer != null && reflectionRenderer != null) {
            IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

            frameBuffer.framebufferClear(IS_RUNNING_ON_MAC);
            frameBuffer.bindFramebuffer(true);

            reflectionRenderer.setupPerspective();

            reflectionRenderer.render(angle, partialTicks, renderTypeBuffer);

            renderTypeBuffer.finish();

            reflectionRenderer.tearDownPerspective();

            frameBuffer.unbindFramebuffer();
        }
    }

    @Override
    public void forceRerender() {
        super.forceRerender();

        lastRenderPartialTicks = -1f;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    /**
     * Bind the reflection texture, if it's there.
     *
     * @return Whether the texture was successfully bound.
     */
    boolean bind() {
        if (frameBuffer != null) {
            frameBuffer.bindFramebufferTexture();
            return true;
        }
        else {
            RenderSystem.bindTexture(0);
            return false;
        }
    }
}
