package com.tomboshoven.minecraft.magicmirror.reflection;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.events.MagicMirrorModifiersUpdatedEvent;
import com.tomboshoven.minecraft.magicmirror.events.MagicMirrorReflectedEntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionManager {
    /**
     * All current reflections.
     */
    private static Map<MagicMirrorCoreBlockEntity, Reflection> reflections = new ConcurrentHashMap<>();
    /**
     * The reflections to maintain in the next iteration.
     */
    private static Map<MagicMirrorCoreBlockEntity, Reflection> reflectionNext = new ConcurrentHashMap<>();

    public static Reflection reflectionForRendering(MagicMirrorCoreBlockEntity blockEntity) {
        Reflection reflection = reflections.computeIfAbsent(blockEntity, Reflection::new);
        reflectionNext.put(blockEntity, reflection);
        return reflection;
    }

    /**
     * Update reflections when mirror is changed.
     */
    @SubscribeEvent
    public static void handleMirrorUpdates(MagicMirrorModifiersUpdatedEvent event) {
        MagicMirrorCoreBlockEntity blockEntity = event.getBlockEntity();
        Reflection reflection = reflections.get(blockEntity);
        if (reflection != null) {
            reflection.update();
        }
    }

    /**
     * Update reflections when reflected entity is changed.
     */
    @SubscribeEvent
    public static void handleMirrorUpdates(MagicMirrorReflectedEntityEvent event) {
        MagicMirrorCoreBlockEntity blockEntity = event.getBlockEntity();
        Reflection reflection = reflections.get(blockEntity);
        Entity reflectedEntity = event.getReflectedEntity();
        if (reflection != null) {
            if (reflectedEntity == null) {
                reflection.stopReflecting();
            } else {
                reflection.setReflectedEntity(reflectedEntity);
            }
        }
    }

    /**
     * Render all reflections that were requested during the previous frame.
     * We're always one second late like this, but it saves us the trouble of making sure we only render the reflections
     * in the frustrum.
     */
    @SubscribeEvent
    public static void renderReflections(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !Minecraft.getInstance().noRender) {
            for (Reflection reflection : reflections.values()) {
                reflection.render(event.renderTickTime);
            }
            // Restore the regular frame buffer
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);

            // Swap out the reflection maps
            Map<MagicMirrorCoreBlockEntity, Reflection> oldReflections = reflections;
            // Deactivate all reflections we no longer need.
            oldReflections.entrySet().stream().filter(entry -> !reflectionNext.containsKey(entry.getKey())).forEach(entry -> entry.getValue().stopReflecting());
            oldReflections.clear();
            reflections = reflectionNext;
            reflectionNext = oldReflections;
        }
    }
}
