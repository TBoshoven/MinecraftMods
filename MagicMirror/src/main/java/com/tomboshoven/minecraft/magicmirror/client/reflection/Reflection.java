package com.tomboshoven.minecraft.magicmirror.client.reflection;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers.ReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers.ReflectionModifiers;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRenderer;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

import static net.minecraft.client.Minecraft.ON_OSX;
import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * A reflection of an entity.
 * This class is mainly responsible for rendering this reflection and making it available as a texture.
 */
public class Reflection {
    // The dimensions of the texture we render the reflection to.
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 128;

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
     * The renderer for the reflection.
     */
    @Nullable
    private ReflectionRendererBase reflectionRenderer;

    /**
     * The number of currently active reflections on the client side.
     */
    static int numActiveReflections;

    /**
     * The block entity to base the reflection on.
     */
    MagicMirrorCoreBlockEntity blockEntity;

    /**
     * The entity that is currently being reflected, if any.
     */
    @Nullable
    Entity reflectedEntity;

    /**
     * The angle in degrees over the Y axis that the reflection should be rotated.
     */
    float angle;

    /**
     * @param blockEntity The block entity corresponding to the mirror that displays the reflection.
     */
    public Reflection(MagicMirrorCoreBlockEntity blockEntity) {
        angle = blockEntity.getBlockState().getValue(HORIZONTAL_FACING).toYRot();
        this.blockEntity = blockEntity;

        Entity entity = blockEntity.getReflectedEntity();
        if (entity != null) {
            setReflectedEntity(entity);
        }

        renderType = new ReflectionRenderType(this);
    }

    /**
     * Get the total number of active reflections in this instance's client thread; used for debugging leaks.
     */
    public static int countActiveReflections() {
        return numActiveReflections;
    }

    /**
     * Stop reflecting any entities.
     * This cleans up all used resources.
     */
    public void stopReflecting() {
        if (reflectedEntity != null) {
            MagicMirrorMod.LOGGER.debug("No longer reflecting {}", reflectedEntity.getName());
            cleanUpRenderer();
            reflectedEntity = null;
            --numActiveReflections;
        }
    }

    /**
     * Construct a new frame buffer to render to.
     */
    void buildFrameBuffer() {
        frameBuffer = new Framebuffer(TEXTURE_WIDTH, TEXTURE_HEIGHT, true, ON_OSX);
        frameBuffer.unbindWrite();
    }

    /**
     * Clean up the current frame buffer.
     */
    void cleanUpFrameBuffer() {
        if (frameBuffer != null) {
            frameBuffer.destroyBuffers();
            frameBuffer = null;
        }
    }

    /**
     * Re-create the reflection renderer.
     * Used when a new modifier is introduced.
     */
    void rebuildRenderer() {
        if (reflectedEntity != null) {
            reflectionRenderer = new ReflectionRenderer(reflectedEntity);
            for (MagicMirrorBlockEntityModifier modifier : blockEntity.getModifiers()) {
                ReflectionModifier reflectionModifier = ReflectionModifiers.forMirrorModifier(modifier.getModifier());
                if (reflectionModifier != null) {
                    reflectionRenderer = reflectionModifier.apply(modifier, reflectionRenderer);
                }
            }
        }
    }

    /**
     * Clean up the current reflection renderer.
     */
    void cleanUpRenderer() {
        reflectionRenderer = null;
    }

    /**
     * @return Which entity is currently being reflected, or null if none.
     */
    @Nullable
    public Entity getReflectedEntity() {
        return reflectedEntity;
    }

    /**
     * Reflect an entity.
     *
     * @param reflectedEntity Which entity to start reflecting.
     */
    public void setReflectedEntity(Entity reflectedEntity) {
        if (this.reflectedEntity != reflectedEntity) {
            MagicMirrorMod.LOGGER.debug("Reflecting {}", reflectedEntity.getName());
            if (this.reflectedEntity == null) {
                ++numActiveReflections;
            }
            this.reflectedEntity = reflectedEntity;
            rebuildRenderer();
        }
    }

    /**
     * Render the reflection of the entity to the texture.
     * This operation unbinds the frame buffer, so rebinding may be required afterward.
     *
     * @param partialTicks The partial ticks, used for rendering smooth animations.
     */
    public void render(float partialTicks) {
        // Create or destroy the framebuffer if needed
        if (reflectedEntity != null && frameBuffer == null) {
            buildFrameBuffer();
        } else if (reflectedEntity == null && frameBuffer != null) {
            cleanUpFrameBuffer();
        }


        if (frameBuffer != null && reflectionRenderer != null) {
            IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

            frameBuffer.clear(ON_OSX);
            frameBuffer.bindWrite(true);

            reflectionRenderer.setUp();

            reflectionRenderer.render(angle, partialTicks, renderTypeBuffer);

            renderTypeBuffer.endBatch();

            reflectionRenderer.tearDown();

            frameBuffer.unbindWrite();
        }
    }

    /**
     * Update the reflection based on changes in the block entity.
     */
    public void update() {
        rebuildRenderer();
    }

    /**
     * @return the render type for rendering the actual reflection.
     */
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
