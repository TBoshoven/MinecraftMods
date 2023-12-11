package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.items.ItemMagicDoorknob;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Top or bottom part of a magic door.
 */
public class BlockMagicDoor extends BlockMagicDoorwayPartBase {
    /**
     * Property describing which part of the door is being represented by this block.
     */
    public static final PropertyEnum<EnumPartType> PART = PropertyEnum.create("part", EnumPartType.class);
    /**
     * Property describing which way the door is facing.
     */
    public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;

    private static final AxisAlignedBB BOUNDING_BOX_WALL_S = new AxisAlignedBB(0, 0, 0, 1, 1, 0.0625);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_N = new AxisAlignedBB(0, 0, 0.9375, 1, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_E = new AxisAlignedBB(0, 0, 0, 0.0625, 1, 1);
    private static final AxisAlignedBB BOUNDING_BOX_WALL_W = new AxisAlignedBB(0.9375, 0, 0, 1, 1, 1);

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        // Return the sound type of the base block, except that placing and removing it are door open and close sounds.
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagicDoor) {
            IBlockState textureBlock = ((TileEntityMagicDoor) tileEntity).getBaseBlockState();
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
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        EnumPartType part = state.getValue(PART);

        // Break the door if the other part is broken.
        if (
                part == EnumPartType.TOP && worldIn.getBlockState(pos.down()).getBlock() != this ||
                        part == EnumPartType.BOTTOM && worldIn.getBlockState(pos.up()).getBlock() != this
        ) {
            // Spawn the doorknob before breaking the block.
            Item doorknob = getDoorknob(worldIn, pos);
            if (doorknob != null) {
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(doorknob, 1, 0));
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
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        breakDoorway(worldIn, pos, state.getValue(FACING));
        super.breakBlock(worldIn, pos, state);
    }

    /**
     * Break the doorway in the area of influence of this block.
     *
     * @param world  The world containing the door
     * @param pos    The position of the door block
     * @param facing The direction the door is facing in (opposite to doorway)
     */
    private void breakDoorway(World world, BlockPos pos, EnumFacing facing) {
        EnumFacing doorwayFacing = facing.getOpposite();

        ItemMagicDoorknob doorknob = getDoorknob(world, pos);
        // If the doorknob can't be found, just go with some high number (32)
        float depth = doorknob == null ? 32 : doorknob.getMaterial().getEfficiency();

        for (int i = 1; i <= depth; ++i) {
            BlockPos blockPos = pos.offset(doorwayFacing, i);
            IBlockState state = world.getBlockState(blockPos);
            if (state.getBlock() == Blocks.blockMagicDoorway) {
                world.destroyBlock(blockPos, false);
            }
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        // Collisions for the door consist of a single box (the knob does not cause collisions).
        EnumFacing facing = state.getValue(FACING);
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        // Return one of the pre-defined bounding boxes.
        switch (state.getValue(FACING)) {
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
                .add(PART)
                .add(FACING)
                .add(TEXTURE_MAIN)
                .add(TEXTURE_HIGHLIGHT)
                .build();
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).getValue() | (state.getValue(FACING).getHorizontalIndex() << 1);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(PART, (meta & 1) == 0 ? EnumPartType.BOTTOM : EnumPartType.TOP)
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta >> 1));
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMagicDoor();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, net.minecraft.init.Blocks.AIR.getDefaultState());
        }
        return true;
    }
}
