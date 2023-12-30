package com.tomboshoven.minecraft.magicmirror.reflection;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

/**
 * A reflection of an entity.
 * This class is mainly responsible for rendering this reflection and making it available as a texture.
 */
public class Reflection {
    /**
     * The number of currently active reflections on the client side.
     */
    static int activeReflectionsClient;

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
     */
    float angle;

    /**
     * @param blockEntity The block entity corresponding to the mirror that displays the reflection.
     */
    public Reflection(MagicMirrorCoreTileEntity blockEntity) {
        this.blockEntity = blockEntity;
        Entity entity = blockEntity.getReflectedEntity();
        if (entity != null) {
            setReflectedEntity(entity);
        }
    }

    /**
     * Get the total number of active reflections in this instance's client thread; used for debugging leaks.
     */
    public static int getActiveReflectionsClient() {
        return activeReflectionsClient;
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
            decrementActiveReflections();
        }
    }

    /**
     * Increment the variable that keeps track of the number of active reflections on the client side.
     */
    void incrementActiveClientReflections() {
        // Nothing to do, since we're server-side.
    }

    /**
     * Decrement the variable that keeps track of the number of active reflections on the client side.
     */
    void decrementActiveReflections() {
        // Nothing to do, since we're server-side.
    }

    /**
     * Construct a new frame buffer to render to.
     */
    void buildFrameBuffer() {
    }

    /**
     * Clean up the current frame buffer.
     */
    void cleanUpFrameBuffer() {
    }

    /**
     * Re-create the reflection renderer.
     * Used when a new modifier is introduced.
     */
    void rebuildRenderer() {
    }

    /**
     * Clean up the current reflection renderer.
     */
    void cleanUpRenderer() {
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
                incrementActiveClientReflections();
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
    }

    /**
     * Update the reflection based on changes in the block entity.
     */
    public void update() {
        rebuildRenderer();
    }

    /**
     * Change the angle of the reflection.
     *
     * @param angle The angle in degrees over the Y axis that the reflection should be rotated.
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }
}
