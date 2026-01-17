package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.Optional;

/**
 * Top or bottom part of a magic door.
 */
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
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        // Return the sound type of the base block, except that placing and removing it are door open and close sounds.
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof MagicDoorBlockEntity) {
            BlockState textureBlock = ((MagicDoorwayPartBaseBlockEntity) tileEntity).getBaseBlockState();
            SoundType actualSoundType = textureBlock.getBlock().getSoundType(textureBlock, world, pos, null);
            return new SoundType(
                    actualSoundType.volume,
                    actualSoundType.pitch,
                    SoundEvents.WOODEN_DOOR_CLOSE,
                    actualSoundType.getStepSound(),
                    SoundEvents.WOODEN_DOOR_OPEN,
                    actualSoundType.getHitSound(),
                    actualSoundType.getFallSound()
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
    private static MagicDoorknobItem getDoorknob(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof MagicDoorBlockEntity) {
            return ((MagicDoorwayPartBaseBlockEntity) tileEntity).getDoorknob();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        EnumPartType part = state.getValue(PART);
        breakDoorway(worldIn, pos, state.getValue(HORIZONTAL_FACING), part);

        if (part == EnumPartType.TOP) {
            TileEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof MagicDoorBlockEntity) {
                MagicDoorBlockEntity magicDoorBlockEntity = (MagicDoorBlockEntity) blockEntity;
                // Spawn the doorknob
                ItemStack doorknobItem = magicDoorBlockEntity.getDoorknobItem()
                        .orElseGet(() -> {
                            Item doorknob = magicDoorBlockEntity.getDoorknob();
                            return doorknob == null ? ItemStack.EMPTY : new ItemStack(doorknob, 1);
                        });
                InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), doorknobItem);
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
     * @param part   The part of the doorway we're breaking
     */
    private void breakDoorway(World world, BlockPos pos, Direction facing, MagicDoorwayPartBaseBlock.EnumPartType part) {
        Direction doorwayFacing = facing.getOpposite();

        MagicDoorknobItem doorknob = getDoorknob(world, pos);
        // If the doorknob can't be found for whatever reason, fall back on the maximum possible value
        double depth = doorknob == null ? MagicDoorknobItem.MAX_DOORWAY_LENGTH : doorknob.getDepth();

        MagicDoorwayBlock magicDoorwayBlock = Blocks.MAGIC_DOORWAY.get();
        for (int i = 1; i <= depth; ++i) {
            BlockPos blockPos = pos.relative(doorwayFacing, i);
            BlockState state = world.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block == magicDoorwayBlock) {
                // If it's a crossing doorway, just remove the one direction but don't actually break the block
                if (state.getValue(MagicDoorwayBlock.OPEN_EAST_WEST) && state.getValue(MagicDoorwayBlock.OPEN_NORTH_SOUTH)) {
                    BlockState newState = state.setValue(facing.getAxis() == Direction.Axis.X ? MagicDoorwayBlock.OPEN_EAST_WEST : MagicDoorwayBlock.OPEN_NORTH_SOUTH, false);
                    if (part == EnumPartType.BOTTOM && state.getValue(MagicDoorwayBlock.OPEN_CROSS_TOP_BOTTOM)) {
                        newState = state.setValue(MagicDoorwayBlock.PART, EnumPartType.TOP).setValue(MagicDoorwayBlock.OPEN_CROSS_TOP_BOTTOM, false);
                    }
                    world.setBlockAndUpdate(blockPos, newState);
                }
                else {
                    magicDoorwayBlock.tryClose(world, blockPos);
                }
            } else if (block == this && state.getValue(HORIZONTAL_FACING) == doorwayFacing) {
                // We found an opposite door; just close the pair together
                world.destroyBlock(blockPos, false);
                break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[state.getValue(HORIZONTAL_FACING).get2DDataValue()];
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PART, HORIZONTAL_FACING);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MagicDoorBlockEntity();
    }

    /**
     * Flip the direction of the doorway in case it has two doors.
     * If two doors are found, they swap their doorknobs.
     * If no second door is found, nothing happens.
     *
     * @param level     The level containing the door
     * @param pos       The position of the top of one of the doors
     * @param direction The direction of the doorway
     */
    private void flipDoorway(World level, BlockPos pos, Direction direction) {
        MagicDoorknobItem doorknob = getDoorknob(level, pos);
        // If the doorknob can't be found for whatever reason, fall back on the maximum possible value
        double depth = doorknob == null ? MagicDoorknobItem.MAX_DOORWAY_LENGTH : doorknob.getDepth();

        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MagicDoorBlockEntity) {
            MagicDoorBlockEntity magicDoorBlockEntity = (MagicDoorBlockEntity) blockEntity;
            for (int i = 1; i <= depth + 1; ++i) {
                BlockPos blockPos = pos.relative(direction, i);
                BlockState state = level.getBlockState(blockPos);
                if (state.getBlock() == this && state.getValue(HORIZONTAL_FACING) == direction) {
                    TileEntity otherBlockEntity = level.getBlockEntity(blockPos);
                    if (otherBlockEntity instanceof MagicDoorBlockEntity) {
                        MagicDoorBlockEntity otherMagicDoorBlockEntity = (MagicDoorBlockEntity) otherBlockEntity;
                        Optional<ItemStack> ownDoorknobOpt = magicDoorBlockEntity.getDoorknobItem();
                        Optional<ItemStack> otherDoorknobOpt = otherMagicDoorBlockEntity.getDoorknobItem();
                        ownDoorknobOpt.ifPresent(ownDoorknob -> otherDoorknobOpt.ifPresent(otherDoorknob -> {
                            magicDoorBlockEntity.setDoorknobItem(otherDoorknob);
                            otherMagicDoorBlockEntity.setDoorknobItem(ownDoorknob);
                        }));
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide) {
            // Swap the doors if possible and necessary, moving the doorknob to this door
            TileEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof MagicDoorBlockEntity) {
                MagicDoorBlockEntity magicDoorBlockEntity = (MagicDoorBlockEntity) blockEntity;
                if (!magicDoorBlockEntity.getDoorknobItem().filter(i -> !i.isEmpty()).isPresent()) {
                    BlockPos topPos = state.getValue(PART) == EnumPartType.TOP ? pos : pos.above();
                    flipDoorway(worldIn, topPos, state.getValue(HORIZONTAL_FACING).getOpposite());
                }
            }
            worldIn.destroyBlock(pos, false);
        }
        return true;
    }
}
