package com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorway;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

public final class BlockColorHandlers {
    @SidedProxy(
            serverSide = "com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers$BlockColorHandlerRegistration",
            clientSide = "com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers$BlockColorHandlerRegistrationClient"
    )
    private static BlockColorHandlerRegistration blockColorHandlerRegistration;

    private BlockColorHandlers() {
    }

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
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    @SideOnly(Side.CLIENT)
    public static class BlockColorHandlerRegistrationClient extends BlockColorHandlerRegistration {
        @Override
        void registerBlockColorHandlers() {
            super.registerBlockColorHandlers();

            BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
            blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                if (worldIn != null && pos != null) {
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    if (tileEntity instanceof TileEntityMagicDoorway) {
                        IBlockState replacedBlock = ((TileEntityMagicDoorway) tileEntity).getReplacedBlock();
                        return blockColors.colorMultiplier(replacedBlock, worldIn, pos, tintIndex);
                    }
                }
                return -1;
            }, Blocks.blockMagicDoorway);
            blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
                if (worldIn != null && pos != null) {
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    if (tileEntity instanceof TileEntityMagicDoor) {
                        IBlockState textureBlock = ((TileEntityMagicDoor) tileEntity).getTextureBlock();
                        return blockColors.colorMultiplier(textureBlock, worldIn, pos, tintIndex);
                    }
                }
                return -1;
            }, Blocks.blockMagicDoor);
        }
    }
}
