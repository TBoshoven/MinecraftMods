package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.BlockEntities;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Manager of all renderers in the mod.
 */
public final class Renderers {
    private Renderers() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Renderers::registerRenderers);
        MinecraftForge.EVENT_BUS.addListener(Renderers::registerReloadListeners);
    }

    /**
     * Register all renderers.
     *
     * @param event The event that triggers this method.
     */
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntities.MAGIC_MIRROR_CORE.get(), BlockEntityMagicMirrorCoreRenderer::new);
        event.registerBlockEntityRenderer(BlockEntities.MAGIC_MIRROR_PART.get(), BlockEntityMagicMirrorPartRenderer::new);
    }

    /**
     * Register all reload listeners.
     *
     * @param event The event that triggers this method.
     */
    private static void registerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(OffModelPlayerRenderers.getInstance());
    }
}
