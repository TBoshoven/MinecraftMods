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

/**
 * Client-side version of the reflection.
 * This version actually renders a reflection.
 */
public class ReflectionClient extends Reflection {
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 128;

    /**
     * Incrementing ID for use in the texture name.
     */
    private static int texId = 0;

    /**
     * The renderer for the reflection.
     */
    @Nullable
    private ReflectionRendererBase reflectionRenderer;

    /**
     * Location of the texture. Since every reflection has a dynamic texture, we generate these.
     */
    private final ResourceLocation textureLocation;
    @Nullable
    private ReflectionTexture reflectionTexture;

    /**
     * Render type for rendering this reflection to the screen.
     * Each reflection has its own texture, and therefore its own render type.
     */
    private final RenderType renderType;

    /**
     * @param angle The absolute angle of the reflection, based on the direction the mirror is facing in.
     * @param blockEntity The block entity corresponding to the mirror that displays the reflection.
     */
    public ReflectionClient(float angle, MagicMirrorCoreBlockEntity blockEntity) {
        super(angle, blockEntity);
        textureLocation = new ResourceLocation(MagicMirrorMod.MOD_ID, String.format(Locale.ROOT, "reflection_%d", texId++));
        // Use "text" render type, which is also what's used by the map renderer.
        renderType = RenderType.text(textureLocation);
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
    void buildTexture() {
        super.buildTexture();

        if (reflectionTexture == null) {
            reflectionTexture = new ReflectionTexture(TEXTURE_WIDTH, TEXTURE_HEIGHT);
            Minecraft.getInstance().getTextureManager().register(textureLocation, reflectionTexture);
        }
    }

    @Override
    void cleanUpTexture() {
        super.cleanUpTexture();

        if (reflectionTexture != null) {
            Minecraft.getInstance().getTextureManager().release(textureLocation);
            reflectionTexture = null;
        }
    }

    @Override
    void rebuildRenderer() {
        super.rebuildRenderer();

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

    @Override
    void cleanUpRenderer() {
        reflectionRenderer = null;
    }

    @Override
    public void render(float partialTicks) {
        super.render(partialTicks);

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
     * @return whether the reflection is available for rendering.
     */
    public boolean isAvailable() {
        return reflectionTexture != null;
    }

    public RenderType getRenderType() {
        return renderType;
    }
}
