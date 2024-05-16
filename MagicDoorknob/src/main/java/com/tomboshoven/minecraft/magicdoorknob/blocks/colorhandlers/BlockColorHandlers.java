package com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Class for registering color handlers in the clients.
 */
public final class BlockColorHandlers {
    @SidedProxy(
            serverSide = "com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers$BlockColorHandlerRegistration",
            clientSide = "com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers$BlockColorHandlerRegistrationClient"
    )
    private static BlockColorHandlerRegistration blockColorHandlerRegistration;

    private BlockColorHandlers() {
    }

    /**
     * Register all color handlers if we're running a client.
     */
    public static void registerColorHandlers() {
        //noinspection StaticVariableUsedBeforeInitialization
        blockColorHandlerRegistration.registerBlockColorHandlers();
    }

    /**
     * Class for block color handler registration.
     * This class is intended for servers. See BlockColorHandlerRegistrationClient for the client version.
     */
    @SuppressWarnings("WeakerAccess")
    public static class BlockColorHandlerRegistration {
        /**
         * Register all block color handlers.
         */
        void registerBlockColorHandlers() {
            // Do nothing on server side
        }
    }

    /**
     * Class for block color handler registration.
     * This class is intended for clients. See BlockColorHandlerRegistration for the server version.
     */
    @SuppressWarnings("unused")
    @SideOnly(Side.CLIENT)
    public static class BlockColorHandlerRegistrationClient extends BlockColorHandlerRegistration {
        @Override
        void registerBlockColorHandlers() {
            super.registerBlockColorHandlers();

            BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

            IBlockColor doorwayBlockColorHandler = new DoorwayBlockColorHandler();
            blockColors.registerBlockColorHandler(doorwayBlockColorHandler, Blocks.blockMagicDoorway);
            blockColors.registerBlockColorHandler(doorwayBlockColorHandler, Blocks.blockMagicDoor);
        }
    }
}
