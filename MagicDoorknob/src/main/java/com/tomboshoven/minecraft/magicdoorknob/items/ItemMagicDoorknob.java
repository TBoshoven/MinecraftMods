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
    static int something;

    /*@Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            IBlockState blockState = worldIn.getBlockState(pos);
            if (blockState.getBlock().hasTileEntity(blockState)) {
                return EnumActionResult.FAIL;
            }
            //worldIn.setBlockState(pos, Blocks.blockMagicDoorway.getDefaultState());
            worldIn.setBlockState(pos, Blocks.blockMagicDoorway.getStateFromMeta((something++) % 16));
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMagicDoorway) {
                ((TileEntityMagicDoorway) tileEntity).setReplacedBlock(blockState);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.SUCCESS;
    }*/

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing.getHorizontalIndex() < 0) {
            return EnumActionResult.FAIL;
        }
        if(!worldIn.isRemote) {
            IBlockState blockState = worldIn.getBlockState(pos);
            if (blockState.getBlock().hasTileEntity(blockState)) {
                return EnumActionResult.FAIL;
            }
            BlockPos doorPos = pos.offset(facing, 1);
            worldIn.setBlockState(doorPos, Blocks.blockMagicDoor.getDefaultState().withProperty(BlockMagicDoor.FACING, facing));
            TileEntity tileEntity = worldIn.getTileEntity(doorPos);
            if (tileEntity instanceof TileEntityMagicDoor) {
                ((TileEntityMagicDoor) tileEntity).setTextureBlock(blockState);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.SUCCESS;
    }
}
