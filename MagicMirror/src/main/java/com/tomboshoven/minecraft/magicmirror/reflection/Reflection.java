package com.tomboshoven.minecraft.magicmirror.reflection;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
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
     * The renderer for the reflection.
     */
    @Nullable
    private ReflectionRendererBase reflectionRenderer;

    /**
     * The number of currently active reflections on the client side.
     */
    static int activeReflections;

    /**
     * The block entity to base the reflection on.
     */
    MagicMirrorCoreTileEntity blockEntity;

    /**
     * The entity that is currently being reflected, if any.
     */
    @Nullable
    Entity reflectedEntity;

    /**
     * The angle in degrees over the Y axis that the reflection should be rotated.
     * The angle in degrees over the Y axis that the reflection should be rotated.
     */
    float angle;

    /**
     * @param blockEntity The block entity corresponding to the mirror that displays the reflection.
     */
    public Reflection(MagicMirrorCoreTileEntity blockEntity) {
        this.blockEntity = blockEntity;
        angle = blockEntity.getBlockState().getValue(HORIZONTAL_FACING).toYRot();
        Entity entity = blockEntity.getReflectedEntity();
        if (entity != null) {
            setReflectedEntity(entity);
        }
    }

    /**
     * Get the total number of active reflections in this instance's client thread; used for debugging leaks.
     */
    public static int getActiveReflections() {
        return activeReflections;
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
            --activeReflections;
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
                ++activeReflections;
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
            frameBuffer.clear(ON_OSX);
            frameBuffer.bindWrite(true);

            reflectionRenderer.setUp();
            reflectionRenderer.render(angle, partialTicks);
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
     * Bind the reflection texture.
     * Before calling this, make sure there is an active reflection, and render() has been called at least once since it
     * became active.
     */
    public void bind() {
        if (frameBuffer == null) {
            throw new RuntimeException("No active reflection");
        }
        frameBuffer.bindRead();
    }
}
