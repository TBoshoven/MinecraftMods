package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorwayPartBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
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
public abstract class BlockMagicDoorwayPartBase extends Block {
    BlockMagicDoorwayPartBase(Block.Properties properties) {
        super(properties);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        // Skip all block breaking textures
        return true;
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        // Use the base block's sound type.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorwayPartBase) {
            BlockState baseBlock = ((TileEntityMagicDoorwayPartBase) tileEntity).getBaseBlockState();
            return baseBlock.getBlock().getSoundType(baseBlock, world, pos, null);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        // Use the base block's light value.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorwayPartBase) {
            return ((TileEntityMagicDoorwayPartBase) tileEntity).getBaseBlockState().getLightValue(world, pos);
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader world, BlockPos pos) {
        // Use the base block's light opacity.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorwayPartBase) {
            return ((TileEntityMagicDoorwayPartBase) tileEntity).getBaseBlockState().getOpacity(world, pos);
        }
        return super.getOpacity(state, world, pos);
    }

    @Override
    public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos) {
        // Use the base block's hardness.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoorwayPartBase) {
            return ((TileEntityMagicDoorwayPartBase) tileEntity).getBaseBlockState().getBlockHardness(world, pos);
        }
        return super.getBlockHardness(blockState, world, pos);
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
    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
        // Since we're basing this block on the "air" material, we have to tell things we can't be replaced when
        // placing blocks
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    /**
     * The doorway has two parts: top and bottom.
     */
    public enum EnumPartType implements IStringSerializable {
        TOP("top", 0),
        BOTTOM("bottom", 1),
        ;

        private final String name;
        private final int value;

        /**
         * @param name  The name of the part.
         * @param value The integer value of the part; used for setting block metadata.
         */
        EnumPartType(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        /**
         * @return The integer value of the part; used for setting block metadata.
         */
        int getValue() {
            return value;
        }
    }
}
