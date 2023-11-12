package com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Class for registering color handlers in the clients.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
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
        BlockColors blockColors = event.getBlockColors();

        BlockColor doorwayBlockColorHandler = new DoorwayBlockColorHandler();
        blockColors.register(doorwayBlockColorHandler, Blocks.MAGIC_DOORWAY.get());
        blockColors.register(doorwayBlockColorHandler, Blocks.MAGIC_DOOR.get());
    }
}
