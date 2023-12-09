package com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
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
    @OnlyIn(Dist.CLIENT)
    private static void registerBlockColorHandlers(ColorHandlerEvent.Block event) {
        BlockColors blockColors = event.getBlockColors();

        IBlockColor doorwayBlockColorHandler = new DoorwayBlockColorHandler();
        blockColors.register(doorwayBlockColorHandler, Blocks.MAGIC_DOORWAY.get());
        blockColors.register(doorwayBlockColorHandler, Blocks.MAGIC_DOOR.get());
    }
}
