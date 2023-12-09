package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.BlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Manager of all renderers in the mod.
 */
@OnlyIn(Dist.CLIENT)
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
    private static void registerRenderers(ModelRegistryEvent event) {
        BlockEntityRenderers.register(BlockEntities.MAGIC_MIRROR_CORE.get(), BlockEntityMagicMirrorCoreRenderer::new);
        BlockEntityRenderers.register(BlockEntities.MAGIC_MIRROR_PART.get(), BlockEntityMagicMirrorPartRenderer::new);
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
