package com.tomboshoven.minecraft.magicmirror.reflection;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifiers;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRenderer;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

import java.util.Locale;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * A reflection of an entity.
 * This class is mainly responsible for rendering this reflection and making it available as a texture.
 */
public class Reflection {
    // The dimensions of the texture we render the reflection to.
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 128;

    /**
     * Incrementing ID for use in the texture name.
     */
    private static int texId = 0;

    /**
     * Location of the texture. Since every reflection has a dynamic texture, we generate these.
     */
    private final ResourceLocation textureLocation;

    /**
     * A reference to the actual texture.
     */
    @Nullable
    private ReflectionTexture reflectionTexture;

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
     * The number of currently active reflections.
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
        textureLocation = new ResourceLocation(MagicMirrorMod.MOD_ID, String.format(Locale.ROOT, "reflection_%d", texId++));

        Entity entity = blockEntity.getReflectedEntity();
        if (entity != null) {
            setReflectedEntity(entity);
        }

        // Use "text" render type, which is also what's used by the map renderer.
        renderType = RenderType.text(textureLocation);
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
    void buildTexture() {
        if (reflectionTexture == null) {
            reflectionTexture = new ReflectionTexture(TEXTURE_WIDTH, TEXTURE_HEIGHT);
            Minecraft.getInstance().getTextureManager().register(textureLocation, reflectionTexture);
        }
    }

    /**
     * Clean up the current frame buffer.
     */
    void cleanUpTexture() {
        if (reflectionTexture != null) {
            Minecraft.getInstance().getTextureManager().release(textureLocation);
            reflectionTexture = null;
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
            for (MagicMirrorBlockEntityModifier modifier : blockEntity.getModifiers()) {
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
        // Create or destroy the frame buffer if needed
        if (reflectedEntity != null && reflectionTexture == null) {
            buildTexture();
        } else if (reflectedEntity == null && reflectionTexture != null) {
            cleanUpTexture();
        }


        if (reflectionTexture != null && reflectionRenderer != null) {
            MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

            reflectionTexture.clear();
            reflectionTexture.bindWrite(true);

            reflectionRenderer.setUp();

            reflectionRenderer.render(angle, partialTicks, renderTypeBuffer);

            renderTypeBuffer.endBatch();

            reflectionRenderer.tearDown();

            reflectionTexture.unbindWrite();
        }
    }

    /**
     * Update the reflection based on changes in the block entity.
     */
    public void update() {
        rebuildRenderer();
    }

    /**
     * @return whether the reflection is available for rendering.
     */
    public boolean isAvailable() {
        return reflectionTexture != null;
    }

    /**
     * @return the render type for rendering the actual reflection.
     */
    public RenderType getRenderType() {
        return renderType;
    }
}
