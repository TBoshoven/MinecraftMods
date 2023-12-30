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
    private static Map<MagicMirrorCoreBlockEntity, ReflectionClient> reflections = new ConcurrentHashMap<>();
    /**
     * The reflections to maintain in the next iteration.
     */
    private static Map<MagicMirrorCoreBlockEntity, ReflectionClient> reflectionNext = new ConcurrentHashMap<>();

    public static ReflectionClient reflectionForRendering(MagicMirrorCoreBlockEntity blockEntity) {
        ReflectionClient reflection = reflections.computeIfAbsent(blockEntity, ReflectionClient::new);
        reflectionNext.put(blockEntity, reflection);
        return reflection;
    }

    /**
     * Update reflections when mirror is changed.
     */
    @SubscribeEvent
    public static void handleMirrorUpdates(MagicMirrorModifiersUpdatedEvent event) {
        MagicMirrorCoreBlockEntity blockEntity = event.getBlockEntity();
        ReflectionClient reflection = reflections.get(blockEntity);
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
        ReflectionClient reflection = reflections.get(blockEntity);
        Entity reflectedEntity = event.getReflectedEntity();
        if (reflection != null) {
            if (reflectedEntity == null) {
                reflection.stopReflecting();
            } else {
                reflection.setReflectedEntity(reflectedEntity);
            }
        }
    }

    @SubscribeEvent
    public static void renderReflections(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !Minecraft.getInstance().noRender) {
            for (Reflection reflection : reflections.values()) {
                reflection.render(event.renderTickTime);
            }
            // Restore the regular frame buffer
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);

            // Swap out the reflection maps
            Map<MagicMirrorCoreBlockEntity, ReflectionClient> oldReflections = reflections;
            oldReflections.entrySet().stream().filter(entry -> !reflectionNext.containsKey(entry.getKey())).forEach(entry -> entry.getValue().stopReflecting());
            oldReflections.clear();
            reflections = reflectionNext;
            reflectionNext = oldReflections;
        }
    }
}
