package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.items.ItemMagicDoorknob;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.state.EnumProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

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

    private static final AxisAlignedBB BOUNDING_BOX_WALL_S = new AxisAlignedBB(0, 0, 0, 1, 1, 0.0625);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_N = new AxisAlignedBB(0, 0, 0.9375, 1, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_E = new AxisAlignedBB(0, 0, 0, 0.0625, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_W = new AxisAlignedBB(0.9375, 0, 0, 1, 1, 1);

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
    public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        // Collisions for the door consist of a single box (the knob does not cause collisions).
        Direction facing = state.get(HORIZONTAL_FACING);
        switch (facing) {
            case NORTH:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WALL_W);
                break;
            case EAST:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WALL_N);
                break;
            case SOUTH:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WALL_E);
                break;
            case WEST:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WALL_S);
                break;
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IEnviromentBlockReader source, BlockPos pos) {
        // Return one of the pre-defined bounding boxes.
        switch (state.get(HORIZONTAL_FACING)) {
            case NORTH:
                return BOUNDING_BOX_WALL_W;
            case EAST:
                return BOUNDING_BOX_WALL_N;
            case SOUTH:
                return BOUNDING_BOX_WALL_E;
        }
        return BOUNDING_BOX_WALL_S;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
        }
        return true;
    }
}
