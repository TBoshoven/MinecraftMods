package com.tomboshoven.minecraft.magicdoorknob.client.blockcolorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;

import javax.annotation.Nullable;

/**
 * Handler for giving doorways more or less the color they should be.
 */
class DoorwayBlockColorHandler implements IBlockColor {
    @Override
    public int getColor(BlockState state, @Nullable IEnviromentBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
        if (worldIn != null && pos != null) {
            BlockColors blockColors = Minecraft.getInstance().getBlockColors();

            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
                BlockState baseBlock = ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState();
                // Return whatever the base block would.
                return blockColors.getColor(baseBlock, worldIn, pos, tintIndex);
            }
        }
        return -1;
    }
}
