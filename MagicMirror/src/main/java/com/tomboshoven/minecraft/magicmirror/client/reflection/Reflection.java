package com.tomboshoven.minecraft.magicmirror.client.reflection;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers.ReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers.ReflectionModifiers;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRenderer;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemModelResolver;
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
    private static int texId;

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
    private ReflectionRendererBase<?> reflectionRenderer;

    /**
     * The number of currently active reflections.
     */
    private static int numActiveReflections;

    /**
     * The block entity to base the reflection on.
     */
    private final MagicMirrorCoreBlockEntity blockEntity;

    /**
     * The render context for the reflection.
     */
    private final RenderContext renderContext;

    /**
     * The entity that is currently being reflected, if any.
     */
    @Nullable
    private
    Entity reflectedEntity;

    /**
     * The angle in degrees over the Y axis that the reflection should be rotated.
     */
    private final float angle;

    /**
     * @param blockEntity The block entity corresponding to the mirror that displays the reflection.
     */
    public Reflection(MagicMirrorCoreBlockEntity blockEntity, RenderContext context) {
        angle = blockEntity.getBlockState().getValue(HORIZONTAL_FACING).toYRot();
        this.blockEntity = blockEntity;
        renderContext = context;
        textureLocation = ResourceLocation.fromNamespaceAndPath(MagicMirrorMod.MOD_ID, String.format(Locale.ROOT, "reflection_%d", texId++));

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
    void stopReflecting() {
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
    private void buildTexture() {
        if (reflectionTexture == null) {
            reflectionTexture = new ReflectionTexture(TEXTURE_WIDTH, TEXTURE_HEIGHT);
            Minecraft.getInstance().getTextureManager().register(textureLocation, reflectionTexture);
        }
    }

    /**
     * Clean up the current frame buffer.
     */
    private void cleanUpTexture() {
        if (reflectionTexture != null) {
            Minecraft.getInstance().getTextureManager().release(textureLocation);
            reflectionTexture = null;
        }
    }

    /**
     * Re-create the reflection renderer.
     * Used when a new modifier is introduced.
     */
    private void rebuildRenderer() {
        if (reflectedEntity != null) {
            reflectionRenderer = new ReflectionRenderer<>(reflectedEntity);
            for (MagicMirrorBlockEntityModifier modifier : blockEntity.getModifiers()) {
                ReflectionModifier reflectionModifier = ReflectionModifiers.forMirrorModifier(modifier.getModifier());
                if (reflectionModifier != null) {
                    reflectionRenderer = reflectionModifier.apply(modifier, reflectionRenderer, renderContext);
                }
            }
        }
    }

    /**
     * Clean up the current reflection renderer.
     */
    private void cleanUpRenderer() {
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
    public final void setReflectedEntity(Entity reflectedEntity) {
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
     * Update the state of the reflection.
     * To be called before rendering.
     *
     * @param partialTicks The partial ticks, used for rendering smooth animations.
     */
    public void updateState(float partialTicks) {
        if (reflectionRenderer != null) {
            reflectionRenderer.updateState(partialTicks);
        }
    }

    /**
     * Render the reflection of the entity to the texture.
     * This operation unbinds the frame buffer, so rebinding may be required afterward.
     */
    public void render() {
        // Create or destroy the frame buffer if needed
        if (reflectedEntity != null && reflectionTexture == null) {
            buildTexture();
        } else if (reflectedEntity == null && reflectionTexture != null) {
            cleanUpTexture();
        }

        if (reflectionTexture != null && reflectionRenderer != null) {
            MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

            reflectionTexture.clear();
            RenderTarget oldMainRenderTarget = reflectionTexture.bindWriteAsMain();

            reflectionRenderer.setUp();

            reflectionRenderer.render(angle, renderTypeBuffer);

            renderTypeBuffer.endBatch();

            reflectionRenderer.tearDown();

            reflectionTexture.unbindWriteAsMain(oldMainRenderTarget);
        }
    }

    /**
     * Update the reflection based on changes in the block entity.
     */
    public void update() {
        rebuildRenderer();
    }

    /**
     * @return Whether the reflection is available for rendering.
     */
    public boolean isAvailable() {
        return reflectionTexture != null;
    }

    /**
     * @return The render type for rendering the actual reflection.
     */
    public RenderType getRenderType() {
        return renderType;
    }

    /**
     * Render context for reflections.
     * Mirrors EntityRendererProvider.Context.
     */
    public record RenderContext(ItemModelResolver itemModelResolver) {
    }
}
