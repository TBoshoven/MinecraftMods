package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Generic functionality for parts of the doorway.
 */
public abstract class MagicDoorwayPartBaseBlock extends Block implements EntityBlock {
    MagicDoorwayPartBaseBlock(Block.Properties properties) {
        super(properties);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        // Use the base block's sound type.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            BlockState baseBlock = ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState();
            return baseBlock.getBlock().getSoundType(baseBlock, world, pos, null);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        // Use the base block's light value.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState().getLightEmission(world, pos);
        }
        return super.getLightEmission(state, world, pos);
    }

    @Override
    public float getFriction(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        // Use the base block's slipperiness.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState().getFriction(world, pos, entity);
        }
        return super.getFriction(state, world, pos, entity);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        // Use the base block's hardness.
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState().getDestroyProgress(player, worldIn, pos);
        }
        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        // Use the base block's hardness.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState().canHarvestBlock(world, pos, player);
        }
        return super.canHarvestBlock(state, world, pos, player);
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
