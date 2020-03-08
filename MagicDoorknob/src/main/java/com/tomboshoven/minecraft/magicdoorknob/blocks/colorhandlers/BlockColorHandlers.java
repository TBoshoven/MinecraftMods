package com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Class for registering color handlers in the clients.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class BlockColorHandlers {
    private BlockColorHandlers() {
    }

    /**
     * Register all color handlers.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerBlockColorHandlers(ColorHandlerEvent.Block event) {
        BlockColors blockColors = event.getBlockColors();

        IBlockColor doorwayBlockColorHandler = new DoorwayBlockColorHandler();
        blockColors.register(doorwayBlockColorHandler, Blocks.blockMagicDoorway);
        blockColors.register(doorwayBlockColorHandler, Blocks.blockMagicDoor);
    }
}
