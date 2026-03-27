package com.tomboshoven.minecraft.magicdoorknob.client.blockcolorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Handler for doorway block tints.
 */
class DoorwayBlockTintSource implements BlockTintSource {
    // The index for the tint source, which maps to the index to use in lookups.
    private final int tintIndex;

    /**
     * @param tintIndex The index for the tint source, which maps to the index to use in lookups.
     */
    public DoorwayBlockTintSource(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    @Override
    public int color(BlockState state) {
        // We can't access the base block without access to the block entity, so there's nothing we can do.
        return -1;
    }

    @Override
    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            BlockState baseBlock = ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState();
            // Return whatever the base block would.
            BlockTintSource baseTintSource = blockColors.getTintSource(baseBlock, tintIndex);
            if (baseTintSource != null) {
                return baseTintSource.colorInWorld(baseBlock, level, pos);
            }
        }
        return -1;
    }

    @Override
    public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            BlockState baseBlock = ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState();
            // Return whatever the base block would.
            BlockTintSource baseTintSource = blockColors.getTintSource(baseBlock, tintIndex);
            if (baseTintSource != null) {
                return baseTintSource.colorAsTerrainParticle(baseBlock, level, pos);
            }
        }
        return -1;
    }
}
