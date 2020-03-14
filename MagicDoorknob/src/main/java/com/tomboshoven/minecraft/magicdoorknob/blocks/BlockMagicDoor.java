package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.items.ItemMagicDoorknob;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.state.EnumProperty;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.util.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Top or bottom part of a magic door.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockMagicDoor extends BlockMagicDoorwayPartBase {
    /**
     * Property describing which part of the door is being represented by this block.
     */
    public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);
    /**
     * Property describing which way the door is facing.
     */
    public static final EnumProperty<Direction> HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(0, 0, 0, 16, 16, 1);
    private static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(0, 0, 15, 16, 16, 16);
    private static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(0, 0, 0, 1, 16, 16);
    private static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(15, 0, 0, 16, 16, 16);

    BlockMagicDoor(Properties properties) {
        super(properties);
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        // Return the sound type of the base block, except that placing and removing it are door open and close sounds.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoor) {
            BlockState textureBlock = ((TileEntityMagicDoor) tileEntity).getBaseBlockState();
            SoundType actualSoundType = textureBlock.getBlock().getSoundType(textureBlock, world, pos, null);
            return new SoundType(
                    actualSoundType.volume,
                    actualSoundType.pitch,
                    SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,
                    actualSoundType.getStepSound(),
                    SoundEvents.BLOCK_WOODEN_DOOR_OPEN,
                    actualSoundType.getHitSound(),
                    actualSoundType.getFallSound()
            );
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

        EnumPartType part = state.get(PART);

        // Break the door if the other part is broken.
        if (
                part == EnumPartType.TOP && worldIn.getBlockState(pos.down()).getBlock() != this ||
                        part == EnumPartType.BOTTOM && worldIn.getBlockState(pos.up()).getBlock() != this
        ) {
            // Spawn the doorknob before breaking the block.
            Item doorknob = getDoorknob(worldIn, pos);
            if (doorknob != null) {
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(doorknob, 1));
            }
            worldIn.destroyBlock(pos, false);
        }
    }

    /**
     * Get the doorknob that opened this door.
     *
     * @param world The world containing the door
     * @param pos   The position of the door block
     * @return The doorknob if it can be found
     */
    @Nullable
    private ItemMagicDoorknob getDoorknob(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoor) {
            return ((TileEntityMagicDoor) tileEntity).getDoorknob();
        }
        return null;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        breakDoorway(worldIn, pos, state.get(HORIZONTAL_FACING));
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    /**
     * Break the doorway in the area of influence of this block.
     *
     * @param world  The world containing the door
     * @param pos    The position of the door block
     * @param facing The direction the door is facing in (opposite to doorway)
     */
    private void breakDoorway(World world, BlockPos pos, Direction facing) {
        Direction doorwayFacing = facing.getOpposite();

        ItemMagicDoorknob doorknob = getDoorknob(world, pos);
        // If the doorknob can't be found, just go with some high number (32)
        float depth = doorknob == null ? 32 : doorknob.getTier().getEfficiency();

        for (int i = 1; i <= depth; ++i) {
            BlockPos blockPos = pos.offset(doorwayFacing, i);
            BlockState state = world.getBlockState(blockPos);
            if (state.getBlock() == Blocks.MAGIC_DOORWAY) {
                world.destroyBlock(blockPos, false);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext selectionContext) {
        switch (state.get(HORIZONTAL_FACING)) {
            case SOUTH:
                return SHAPE_SOUTH;
            case NORTH:
                return SHAPE_NORTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
        }
        return super.getShape(state, world, pos, selectionContext);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PART, HORIZONTAL_FACING);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityMagicDoor();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
        }
        return true;
    }
}
