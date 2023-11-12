package com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.bus.api.IEventBus;

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

    /**
     * Register all color handlers.
     */
    @SubscribeEvent
    private static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        BlockColors blockColors = event.getBlockColors();

        BlockColor doorwayBlockColorHandler = new DoorwayBlockColorHandler();
        blockColors.register(doorwayBlockColorHandler, Blocks.MAGIC_DOORWAY.get());
        blockColors.register(doorwayBlockColorHandler, Blocks.MAGIC_DOOR.get());
    }
}
