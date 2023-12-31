package com.tomboshoven.minecraft.magicdoorknob.client.blockcolorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.client.color.block.BlockColor;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Class for registering color handlers in the clients.
 */
public final class BlockColorHandlers {
    private BlockColorHandlers() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(BlockColorHandlers::registerBlockColorHandlers);
    }

    /**
     * Register all color handlers.
     */
    private static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        BlockColor doorwayBlockColorHandler = new DoorwayBlockColorHandler();
        event.register(doorwayBlockColorHandler, Blocks.MAGIC_DOORWAY.get());
        event.register(doorwayBlockColorHandler, Blocks.MAGIC_DOOR.get());
    }
}
