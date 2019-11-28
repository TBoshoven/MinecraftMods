package com.tomboshoven.minecraft.magicdoorknob.items;

import com.tomboshoven.minecraft.magicdoorknob.blocks.BlockMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.blocks.BlockMagicDoorway;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorway;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMagicDoorknob extends Item {
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (canPlaceDoor(worldIn, pos, facing)) {
                placeDoor(worldIn, pos, facing);
                placeDoorway(worldIn, pos, facing);
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.SUCCESS;
    }

    void placeDoor(World world, BlockPos pos, EnumFacing facing) {
        world.setBlockState(
                pos.offset(facing),
                Blocks.blockMagicDoor.getDefaultState()
                        .withProperty(BlockMagicDoor.FACING, facing)
                        .withProperty(BlockMagicDoor.PART, BlockMagicDoor.EnumPartType.TOP)
        );
        TileEntity topTileEntity = world.getTileEntity(pos.offset(facing));
        if (topTileEntity instanceof TileEntityMagicDoor) {
            ((TileEntityMagicDoor) topTileEntity).setTextureBlock(world.getBlockState(pos));
        }
        world.setBlockState(
                pos.offset(facing).down(),
                Blocks.blockMagicDoor.getDefaultState()
                        .withProperty(BlockMagicDoor.FACING, facing)
                        .withProperty(BlockMagicDoor.PART, BlockMagicDoor.EnumPartType.BOTTOM)
        );
        TileEntity bottomTileEntity = world.getTileEntity(pos.offset(facing).down());
        if (bottomTileEntity instanceof TileEntityMagicDoor) {
            ((TileEntityMagicDoor) bottomTileEntity).setTextureBlock(world.getBlockState(pos.down()));
        }
    }

    void placeDoorway(World world, BlockPos pos, EnumFacing facing) {
        EnumFacing doorwayFacing = facing.getOpposite();
        boolean isNorthSouth = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH;
        for (int i = 0; i < 10; ++i) {
            placeDoorwayElement(world, pos.offset(doorwayFacing, i), isNorthSouth, BlockMagicDoorway.EnumPartType.TOP);
            placeDoorwayElement(world, pos.offset(doorwayFacing, i).down(), isNorthSouth, BlockMagicDoorway.EnumPartType.BOTTOM);
        }
    }

    void placeDoorwayElement(World world, BlockPos pos, boolean isNorthSouth, BlockMagicDoorway.EnumPartType part) {
        if (isReplaceable(world, pos)) {
            IBlockState state = world.getBlockState(pos);
            world.setBlockState(pos, Blocks.blockMagicDoorway.getDefaultState().withProperty(BlockMagicDoorway.OPEN_NORTH_SOUTH, isNorthSouth).withProperty(BlockMagicDoorway.OPEN_EAST_WEST, !isNorthSouth).withProperty(BlockMagicDoorway.PART, part));

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMagicDoorway) {
                ((TileEntityMagicDoorway) tileEntity).setReplacedBlock(state);
            }
        }
    }

    boolean canPlaceDoor(World world, BlockPos pos, EnumFacing facing) {
        if (!isReplaceable(world, pos) || !isReplaceable(world, pos.down())) {
            return false;
        }
        if (!isEmpty(world, pos.offset(facing)) || !isEmpty(world, pos.offset(facing).down())) {
            return false;
        }
        return true;
    }

    boolean isEmpty(World world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock().isAir(blockState, world, pos)) {
            return true;
        }

        if (blockState.getBlock().isReplaceable(world, pos)) {
            return true;
        }

        return false;
    }

    boolean isReplaceable(World world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock().hasTileEntity(blockState)) {
            return false;
        }
        // TODO: Whitelist, Evaluate hardness
        return true;
    }
}
