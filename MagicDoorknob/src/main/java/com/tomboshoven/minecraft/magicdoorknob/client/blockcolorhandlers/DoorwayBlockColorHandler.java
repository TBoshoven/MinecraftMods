package com.tomboshoven.minecraft.magicdoorknob.client.blockcolorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Handler for giving doorways more or less the color they should be.
 */
class DoorwayBlockColorHandler implements BlockColor {
    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
        if (worldIn != null && pos != null) {
            BlockColors blockColors = Minecraft.getInstance().getBlockColors();

            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
                BlockState baseBlock = ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState();
                // Return whatever the base block would.
                return blockColors.getColor(baseBlock, worldIn, pos, tintIndex);
            }
        }
        return -1;
    }
}