package com.tomboshoven.minecraft.magicmirror.reflection;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRenderer;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX;
import static net.minecraft.client.Minecraft.ON_OSX;

/**
 * Client-side version of the reflection.
 * This version actually renders a reflection.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
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
    private RenderTarget frameBuffer;

    /**
     * Render type for rendering this reflection to the screen.
     * Each reflection has its own texture, and therefore its own render type.
     */

    private final RenderType renderType;

    /**
     * Texture state shard for the reflection.
     *
     * This is a custom one that enables texturing, but doesn't actually bind any textures.
     * The texture binding is done in the transparency state, because that's where failure is handled.
     */
    private static class TextureState extends RenderStateShard.EmptyTextureStateShard {
        public TextureState() {
            // Textures will be set later
            super(RenderSystem::enableTexture, () -> {});
        }
    }

    private class TransparencyState extends RenderStateShard.TransparencyStateShard {
        public TransparencyState() {
            super("reflection_transparency", () -> {
                RenderSystem.enableBlend();

                if(ReflectionClient.this.bind()) {
                    RenderSystem.defaultBlendFunc();
                }
                else {
                    // If we couldn't bind the texture, just render nothing
                    RenderSystem.blendFuncSeparate(SourceFactor.ZERO, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
                }
            }, () -> {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            });
        }
    }

    public ReflectionClient() {
        renderType = RenderType.create(
                String.format("reflection[%d]", hashCode()),
                POSITION_COLOR_TEX,
                VertexFormat.Mode.QUADS,
                64,
                true,
                true,
                RenderType.CompositeState.builder()
                        .setTextureState(new TextureState())
                        .setTransparencyState(new TransparencyState())
                        .createCompositeState(true)
        );
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

        frameBuffer = new TextureTarget(TEXTURE_WIDTH, TEXTURE_HEIGHT, true, ON_OSX);
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


        if (frameBuffer != null && reflectionRenderer != null) {
            MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

            frameBuffer.clear(ON_OSX);
            frameBuffer.bindWrite(true);

            reflectionRenderer.setUp();

            reflectionRenderer.render(angle, partialTicks, renderTypeBuffer);

            renderTypeBuffer.endBatch();

            reflectionRenderer.tearDown();

            frameBuffer.unbindWrite();
        }
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
            frameBuffer.bindRead();
            return true;
        }
        else {
            RenderSystem.bindTexture(0);
            return false;
        }
    }
}
