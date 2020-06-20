package com.tomboshoven.minecraft.magicmirror.reflection;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

/**
 * Class overseeing which reflections need to be re-rendered.
 * There is a significant performance benefit to not rendering mirrors outside the frustrum, but replicating that logic
 * seems error-prone.
 * Instead, we re-render the reflection if the reflection was requested in the *previous* frame.
 * Downside of this is that we're always one frame late, but that should not be very noticeable.
 */
public final class ReflectionClientUpdater {
    private static final Set<ReflectionClient> toRerender = Sets.newConcurrentHashSet();

    public static void markViewed(ReflectionClient reflection) {
        toRerender.add(reflection);
    }

    @SubscribeEvent
    public static void renderReflections(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !Minecraft.getInstance().skipRenderWorld) {
            for(Reflection reflection : toRerender) {
                reflection.render(event.renderTickTime);
            }
            // Restore the regular frame buffer
            Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);

            toRerender.clear();
        }
    }
}
