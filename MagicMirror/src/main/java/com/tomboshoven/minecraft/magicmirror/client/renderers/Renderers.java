package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorPartTileEntity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Manager of all renderers in the mod.
 */
public final class Renderers {
    private Renderers() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Renderers::registerRenderers);
    }

    private static void registerRenderers(ModelRegistryEvent event) {
        TileEntityMagicMirrorRenderer renderer = new TileEntityMagicMirrorRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(MagicMirrorCoreTileEntity.class, renderer);
        ClientRegistry.bindTileEntitySpecialRenderer(MagicMirrorPartTileEntity.class, renderer);
    }
}
