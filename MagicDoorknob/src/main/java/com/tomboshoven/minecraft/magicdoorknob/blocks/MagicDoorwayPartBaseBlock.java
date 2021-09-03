package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Generic functionality for parts of the doorway.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MagicDoorwayPartBaseBlock extends Block implements EntityBlock {
    MagicDoorwayPartBaseBlock(Block.Properties properties) {
        super(properties);
    }

    // TODO
//    @Override
//    public boolean addDestroyEffects(BlockState state, Level world, BlockPos pos, ParticleEngine manager) {
//        // Skip all block breaking textures
//        return true;
//    }

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
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        // Use the base block's light value.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState().getLightEmission(world, pos);
        }
        return super.getLightEmission(state, world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        // Use the base block's light opacity.
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState().getLightBlock(worldIn, pos);
        }
        return super.getLightBlock(state, worldIn, pos);
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

    @SuppressWarnings("deprecation")
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        // Use the base block's hardness.
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorwayPartBaseBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState().getDestroyProgress(player, worldIn, pos);
        }
        return super.getDestroyProgress(state, player, worldIn, pos);
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
