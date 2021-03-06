package com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.MagicDoorwayPartBaseTileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Handler for giving doorways more or less the color they should be.
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class DoorwayBlockColorHandler implements IBlockColor {
    @Override
    public int getColor(BlockState state, @Nullable IBlockDisplayReader worldIn, @Nullable BlockPos pos, int tintIndex) {
        if (worldIn != null && pos != null) {
            BlockColors blockColors = Minecraft.getInstance().getBlockColors();

            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
                BlockState baseBlock = ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState();
                // Return whatever the base block would.
                return blockColors.getColor(baseBlock, worldIn, pos, tintIndex);
            }
        }
        return -1;
    }
}
