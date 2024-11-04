package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.BlockEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Manager of all renderers in the mod.
 */
public final class Renderers {
    private Renderers() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Renderers::registerRenderers);
        OffModelRenderers.register(eventBus);
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
}
