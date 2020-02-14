package com.tomboshoven.minecraft.magicdoorknob.items;

import com.tomboshoven.minecraft.magicdoorknob.blocks.BlockMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.blocks.BlockMagicDoorway;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorway;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.IItemStackTextureMapperProvider;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.ITextureMapper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemMagicDoorknob extends Item implements IItemStackTextureMapperProvider {
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
                return EnumActionResult.FAIL;
            }
            if (canPlaceDoor(worldIn, pos, facing)) {
                placeDoor(worldIn, pos, facing);
                placeDoorway(worldIn, pos, facing);
                player.getHeldItem(hand).shrink(1);
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.SUCCESS;
    }

    private static void placeDoor(World world, BlockPos pos, EnumFacing facing) {
        BlockPos doorPos = pos.offset(facing);
        world.setBlockState(
                doorPos,
                Blocks.blockMagicDoor.getDefaultState()
                        .withProperty(BlockMagicDoor.FACING, facing)
                        .withProperty(BlockMagicDoor.PART, BlockMagicDoor.EnumPartType.TOP)
        );
        TileEntity topTileEntity = world.getTileEntity(doorPos);
        if (topTileEntity instanceof TileEntityMagicDoor) {
            ((TileEntityMagicDoor) topTileEntity).setTextureBlock(world.getBlockState(pos));
        }
        world.setBlockState(
                doorPos.down(),
                Blocks.blockMagicDoor.getDefaultState()
                        .withProperty(BlockMagicDoor.FACING, facing)
                        .withProperty(BlockMagicDoor.PART, BlockMagicDoor.EnumPartType.BOTTOM)
        );
        TileEntity bottomTileEntity = world.getTileEntity(doorPos.down());
        if (bottomTileEntity instanceof TileEntityMagicDoor) {
            ((TileEntityMagicDoor) bottomTileEntity).setTextureBlock(world.getBlockState(pos.down()));
        }
        world.checkLightFor(EnumSkyBlock.BLOCK, doorPos);
        world.checkLightFor(EnumSkyBlock.BLOCK, doorPos.down());
    }

    private static void placeDoorway(World world, BlockPos pos, EnumFacing facing) {
        EnumFacing doorwayFacing = facing.getOpposite();
        boolean isNorthSouth = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH;
        for (int i = 0; i < 10; ++i) {
            BlockPos elementPos = pos.offset(doorwayFacing, i);
            if (
                    (isReplaceable(world, elementPos) && !isEmpty(world, elementPos)) ||
                    (isReplaceable(world, elementPos.down()) && !isEmpty(world, elementPos.down()))
            ) {
                placeDoorwayElement(world, elementPos, isNorthSouth, BlockMagicDoorway.EnumPartType.TOP);
                placeDoorwayElement(world, elementPos.down(), isNorthSouth, BlockMagicDoorway.EnumPartType.BOTTOM);
            }
            else {
                // Stop iterating if we've hit two empty blocks
                break;
            }
        }
    }

    private static void placeDoorwayElement(World world, BlockPos pos, boolean isNorthSouth, BlockMagicDoorway.EnumPartType part) {
        if (isReplaceable(world, pos)) {
            IBlockState state = world.getBlockState(pos);
            world.setBlockState(pos, Blocks.blockMagicDoorway.getDefaultState().withProperty(BlockMagicDoorway.OPEN_NORTH_SOUTH, isNorthSouth).withProperty(BlockMagicDoorway.OPEN_EAST_WEST, !isNorthSouth).withProperty(BlockMagicDoorway.PART, part));

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMagicDoorway) {
                ((TileEntityMagicDoorway) tileEntity).setReplacedBlock(state);
            }

            world.checkLightFor(EnumSkyBlock.BLOCK, pos);
        }
    }

    private static boolean canPlaceDoor(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        if (!isReplaceable(world, pos) || !isReplaceable(world, pos.down())) {
            return false;
        }
        return isEmpty(world, pos.offset(facing)) && isEmpty(world, pos.offset(facing).down());
    }

    private static boolean isEmpty(IBlockAccess world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock().isAir(blockState, world, pos)) {
            return true;
        }

        return blockState.getBlock().isReplaceable(world, pos);
    }

    private static boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock().hasTileEntity(blockState)) {
            return false;
        }
        // TODO: Whitelist, Evaluate hardness
        return true;
    }

    @Override
    public ITextureMapper getTextureMapper(ItemStack stack) {
        return (spriteToMap, blockState) -> {
            String name = spriteToMap.getIconName();

            return new ResourceLocation("minecraft:blocks/redstone_block");
        };
    }
}
