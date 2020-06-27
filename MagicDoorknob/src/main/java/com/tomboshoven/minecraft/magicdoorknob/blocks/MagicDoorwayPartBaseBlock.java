package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.MagicDoorwayPartBaseTileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
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
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        // Skip all block breaking textures
        return true;
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        // Use the base block's sound type.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            BlockState baseBlock = ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState();
            return baseBlock.getBlock().getSoundType(baseBlock, world, pos, null);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        // Use the base block's light value.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            return ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState().getLightValue(world, pos);
        }
        return super.getLightValue(state, world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
        // Use the base block's light opacity.
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            return ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState().getOpacity(worldIn, pos);
        }
        return super.getOpacity(state, worldIn, pos);
    }

    @Override
    public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        // Use the base block's slipperiness.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof MagicDoorwayPartBaseTileEntity) {
            return ((MagicDoorwayPartBaseTileEntity) tileEntity).getBaseBlockState().getSlipperiness(world, pos, entity);
        }
        return super.getSlipperiness(state, world, pos, entity);
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
    public enum EnumPartType implements IStringSerializable {
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
        public String func_176610_l() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
