package com.tomboshoven.minecraft.magicdoorknob.client.clientextensions;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class ClientExtensions {
    private ClientExtensions() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ClientExtensions::registerClientBlockExtensions);
    }

    /**
     * Register all color handlers.
     */
    private static void registerClientBlockExtensions(RegisterClientExtensionsEvent event) {
        event.registerBlock(new MagicDoorwayClientBlockExtensions(), Blocks.MAGIC_DOOR.get(), Blocks.MAGIC_DOORWAY.get());
    }
}
