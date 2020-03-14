package com.tomboshoven.minecraft.magicmirror.reflection;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifier;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * A reflection of an entity.
 * This class is mainly responsible for rendering this reflection and making it available as a texture.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Reflection {
    /**
     * The number of currently active reflections on the client side.
     */
    static int activeReflectionsClient = 0;

    /**
     * An ordered list of all the modifiers.
     */
    final List<ReflectionModifier> modifiers = Lists.newArrayList();

    /**
     * The entity that is currently being reflected, if any.
     */
    @Nullable
    private Entity reflectedEntity;

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
            ModMagicMirror.LOGGER.debug("No longer reflecting {}", reflectedEntity.getName());
            cleanUpFrameBuffer();
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
            ModMagicMirror.LOGGER.debug("Reflecting {}", reflectedEntity.getName());
            if (this.reflectedEntity == null) {
                buildFrameBuffer();
                incrementActiveClientReflections();
            }
            this.reflectedEntity = reflectedEntity;
            rebuildRenderer();
        }
    }

    /**
     * Add a new modifier to the reflection.
     *
     * @param modifier The modifier to be added. Must be valid in combination with the existing ones.
     */
    public void addModifier(ReflectionModifier modifier) {
        modifiers.add(modifier);
        rebuildRenderer();
    }

    /**
     * Remove an existing modifier from the reflection.
     *
     * @param modifier The modifier to be removed. Must be one of the current modifiers of this reflection.
     */
    public void removeModifier(ReflectionModifier modifier) {
        modifiers.remove(modifier);
        rebuildRenderer();
    }

    /**
     * Render the reflection of the entity to the texture.
     * This operation unbinds the frame buffer, so rebinding may be required afterward.
     *
     * @param facing       The direction the mirror is facing in; used for determining which side of the reflection to
     *                     draw.
     * @param partialTicks The partial ticks, used for rendering smooth animations.
     */
    public void render(Direction facing, float partialTicks) {
    }

    /**
     * Force the next render operation to re-render the texture.
     * Because of partialTick optimization, this should be called each tick, before starting to render.
     */
    public void forceRerender() {
    }

    /**
     * Bind the reflection texture.
     * Before calling this, make sure there is an active reflection, and render() has been called at least once since it
     * became active.
     */
    public void bind() {
    }
}
