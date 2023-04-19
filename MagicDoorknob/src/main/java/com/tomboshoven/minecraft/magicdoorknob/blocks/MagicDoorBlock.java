package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.ForgeSoundType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Top or bottom part of a magic door.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicDoorBlock extends MagicDoorwayPartBaseBlock {
    /**
     * Property describing which part of the door is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);
    /**
     * Property describing which way the door is facing.
     */
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Indexed by horizontal index
    private static final VoxelShape[] SHAPES = {
            Block.box(0, 0, 0, 1, 16, 16),
            Block.box(0, 0, 0, 16, 16, 1),
            Block.box(15, 0, 0, 16, 16, 16),
            Block.box(0, 0, 15, 16, 16, 16),
    };

    MagicDoorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        // Return the sound type of the base block, except that placing and removing it are door open and close sounds.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorBlockEntity) {
            BlockState textureBlock = ((MagicDoorwayPartBaseBlockEntity) blockEntity).getBaseBlockState();
            SoundType actualSoundType = textureBlock.getBlock().getSoundType(textureBlock, world, pos, null);
            return new ForgeSoundType(
                    actualSoundType.volume,
                    actualSoundType.pitch,
                    () -> SoundEvents.WOODEN_DOOR_CLOSE,
                    actualSoundType::getStepSound,
                    () -> SoundEvents.WOODEN_DOOR_OPEN,
                    actualSoundType::getHitSound,
                    actualSoundType::getFallSound
            );
        }
        return super.getSoundType(state, world, pos, entity);
    }

    /**
     * Get the doorknob that opened this door.
     *
     * @param world The world containing the door
     * @param pos   The position of the door block
     * @return The doorknob if it can be found
     */
    @Nullable
    private static MagicDoorknobItem getDoorknob(BlockGetter world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) blockEntity).getDoorknob();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        breakDoorway(worldIn, pos, state.getValue(HORIZONTAL_FACING));

        if (state.getValue(PART) == EnumPartType.TOP) {
            // Spawn the doorknob
            Item doorknob = getDoorknob(worldIn, pos);
            if (doorknob != null) {
                Containers.dropItemStack(worldIn, pos.getX(), pos.getY() - .5f, pos.getZ(), new ItemStack(doorknob, 1));
            }

            // Break the bottom part
            worldIn.destroyBlock(pos.below(), false);
        } else {
            // Break the top part
            worldIn.destroyBlock(pos.above(), false);
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    /**
     * Break the doorway in the area of influence of this block.
     *
     * @param world  The world containing the door
     * @param pos    The position of the door block
     * @param facing The direction the door is facing in (opposite to doorway)
     */
    private static void breakDoorway(Level world, BlockPos pos, Direction facing) {
        Direction doorwayFacing = facing.getOpposite();

        MagicDoorknobItem doorknob = getDoorknob(world, pos);
        // If the doorknob can't be found for whatever reason, fall back on the maximum possible value
        double depth = doorknob == null ? MagicDoorknobItem.MAX_DOORWAY_LENGTH : doorknob.getDepth();

        for (int i = 1; i <= depth; ++i) {
            BlockPos blockPos = pos.relative(doorwayFacing, i);
            BlockState state = world.getBlockState(blockPos);
            if (state.getBlock() == Blocks.MAGIC_DOORWAY.get()) {
                world.destroyBlock(blockPos, false);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(HORIZONTAL_FACING).get2DDataValue()];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MagicDoorBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide) {
            worldIn.destroyBlock(pos, false);
        }
        return InteractionResult.SUCCESS;
    }
}
