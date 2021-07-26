package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.MagicDoorwayPartBaseTileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Generic functionality for parts of the doorway.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MagicDoorwayPartBaseBlock extends Block {
    MagicDoorwayPartBaseBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level world, BlockPos pos, ParticleEngine manager) {
        // Skip all block breaking textures
        return true;
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        // Use the base block's sound type.
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            BlockState baseBlock = ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState();
            return baseBlock.getBlock().getSoundType(baseBlock, world, pos, null);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public int getLightValue(BlockState state, BlockGetter world, BlockPos pos) {
        // Use the base block's light value.
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            return ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState().getLightValue(world, pos);
        }
        return super.getLightValue(state, world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        // Use the base block's light opacity.
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            return ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState().getLightBlock(worldIn, pos);
        }
        return super.getLightBlock(state, worldIn, pos);
    }

    @Override
    public float getSlipperiness(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        // Use the base block's slipperiness.
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            return ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState().getSlipperiness(world, pos, entity);
        }
        return super.getSlipperiness(state, world, pos, entity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        // Use the base block's hardness.
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            return ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState().getDestroyProgress(player, worldIn, pos);
        }
        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        // We don't have information about the base block here.
        // Always allow breaking.
        return -1;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        // We don't have information about the base block here.
        // We allow breaking with any tool.
        return null;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    /**
     * The doorway has two parts: top and bottom.
     */
    public enum EnumPartType implements StringRepresentable {
        TOP("top"),
        BOTTOM("bottom"),
        ;

        private final String name;

        /**
         * @param name The name of the part.
         */
        EnumPartType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
