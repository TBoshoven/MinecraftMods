package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.BlockEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

/**
 * Manager of all renderers in the mod.
 */
public final class Renderers {
    private Renderers() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Renderers::registerRenderers);
        NeoForge.EVENT_BUS.addListener(Renderers::registerReloadListeners);
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
