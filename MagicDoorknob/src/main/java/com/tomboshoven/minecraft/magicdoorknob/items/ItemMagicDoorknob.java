package com.tomboshoven.minecraft.magicdoorknob.items;

import com.tomboshoven.minecraft.magicdoorknob.blocks.BlockMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.blocks.BlockMagicDoorway;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorway;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.IItemStackTextureMapperProvider;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.ITextureMapper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic doorknob that allows you to open doors that don't exist.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemMagicDoorknob extends Item implements IItemStackTextureMapperProvider {
    // The main texture of the item
    private final ResourceLocation mainTextureLocation;
    // The name of the type of item (used in NBT data; do not modify)
    private String typeName;
    // The item material, used for determining doorway generation properties
    private ToolMaterial material;

    /**
     * @param typeName            The main texture of the item
     * @param material            The item material, used for determining doorway generation properties
     * @param mainTextureLocation The name of the type of item (used in NBT data; do not modify)
     */
    public ItemMagicDoorknob(String typeName, ToolMaterial material, ResourceLocation mainTextureLocation) {
        this.typeName = typeName;
        this.material = material;
        this.mainTextureLocation = mainTextureLocation;
    }

    /**
     * @param world The world to check
     * @param pos   The position to check
     * @return Whether the block can be replaced by a door or doorway
     */
    private static boolean isEmpty(IBlockAccess world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock().isAir(blockState, world, pos)) {
            return true;
        }

        return blockState.getBlock().isReplaceable(world, pos);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            // Only sideways doors right now
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

    /**
     * Place a door at the given position.
     * This does not do any checks to see whether it's allowed.
     *
     * @param world  The world to place the door in
     * @param pos    The position of the top part of the door
     * @param facing The direction the door should be facing
     */
    private void placeDoor(World world, BlockPos pos, EnumFacing facing) {
        BlockPos doorPos = pos.offset(facing);
        world.setBlockState(
                doorPos,
                Blocks.blockMagicDoor.getDefaultState()
                        .withProperty(BlockMagicDoor.FACING, facing)
                        .withProperty(BlockMagicDoor.PART, BlockMagicDoor.EnumPartType.TOP)
        );
        TileEntity topTileEntity = world.getTileEntity(doorPos);
        if (topTileEntity instanceof TileEntityMagicDoor) {
            ((TileEntityMagicDoor) topTileEntity).setBaseBlockState(world.getBlockState(pos));
            ((TileEntityMagicDoor) topTileEntity).setDoorknob(this);
        }
        world.setBlockState(
                doorPos.down(),
                Blocks.blockMagicDoor.getDefaultState()
                        .withProperty(BlockMagicDoor.FACING, facing)
                        .withProperty(BlockMagicDoor.PART, BlockMagicDoor.EnumPartType.BOTTOM)
        );
        TileEntity bottomTileEntity = world.getTileEntity(doorPos.down());
        if (bottomTileEntity instanceof TileEntityMagicDoor) {
            ((TileEntityMagicDoor) bottomTileEntity).setBaseBlockState(world.getBlockState(pos.down()));
            ((TileEntityMagicDoor) bottomTileEntity).setDoorknob(this);
        }
        world.playSound(null, doorPos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
    }

    /**
     * Place a doorway that starts at the given position.
     * This does not do any checks to see whether it's allowed.
     *
     * @param world  The world to place the doorway in
     * @param pos    The position of the top part of the starting blocks of the doorway
     * @param facing The direction the door is facing (outward from the doorway)
     */
    private void placeDoorway(World world, BlockPos pos, EnumFacing facing) {
        EnumFacing doorwayFacing = facing.getOpposite();
        boolean isNorthSouth = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH;
        float depth = material.getEfficiency();
        for (int i = 0; i < depth; ++i) {
            BlockPos elementPos = pos.offset(doorwayFacing, i);
            if (
                    (isReplaceable(world, elementPos) && !isEmpty(world, elementPos)) ||
                            (isReplaceable(world, elementPos.down()) && !isEmpty(world, elementPos.down()))
            ) {
                placeDoorwayElement(world, elementPos, isNorthSouth, BlockMagicDoorway.EnumPartType.TOP);
                placeDoorwayElement(world, elementPos.down(), isNorthSouth, BlockMagicDoorway.EnumPartType.BOTTOM);
            } else {
                // Stop iterating if we've hit two empty blocks
                break;
            }
        }
    }

    /**
     * Place a single doorway element.
     *
     * @param world        The world to place the doorway in
     * @param pos          The position of the top part of the starting blocks of the doorway
     * @param isNorthSouth Whether this is a north-south-facing doorway (as opposed to east-west)
     * @param part         Whether this is the top or bottom part
     */
    private void placeDoorwayElement(World world, BlockPos pos, boolean isNorthSouth, BlockMagicDoorway.EnumPartType part) {
        if (isReplaceable(world, pos)) {
            IBlockState state = world.getBlockState(pos);
            world.setBlockState(pos, Blocks.blockMagicDoorway.getDefaultState().withProperty(BlockMagicDoorway.OPEN_NORTH_SOUTH, isNorthSouth).withProperty(BlockMagicDoorway.OPEN_EAST_WEST, !isNorthSouth).withProperty(BlockMagicDoorway.PART, part));

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityMagicDoorway) {
                ((TileEntityMagicDoorway) tileEntity).setBaseBlockState(state);
                ((TileEntityMagicDoorway) tileEntity).setDoorknob(this);
            }

            world.checkLightFor(EnumSkyBlock.BLOCK, pos);
        }
    }

    /**
     * Verify whether a door can be placed at the given position.
     *
     * @param world  The world to analyze
     * @param pos    The position in the world of the block that will be turned into a door
     * @param facing The direction the door will be facing
     * @return Whether a door can be placed at the given position.
     */
    private boolean canPlaceDoor(World world, BlockPos pos, EnumFacing facing) {
        if (!isReplaceable(world, pos) || !isReplaceable(world, pos.down())) {
            return false;
        }
        return isEmpty(world, pos.offset(facing)) && isEmpty(world, pos.offset(facing).down());
    }

    /**
     * Check whether this doorknob can replace the given block by a door or doorway.
     *
     * @param world The world to analyze
     * @param pos   The position to check
     * @return Whether this doorknob can replace the given block by a door or doorway
     */
    private boolean isReplaceable(World world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.hasTileEntity(blockState)) {
            return false;
        }
        // Blocks like bedrock use this to prevent interactions
        if (blockState.getBlockHardness(world, pos) < 0) {
            return false;
        }
        return block.getHarvestLevel(blockState) <= material.getHarvestLevel();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ITextureMapper getTextureMapper(ItemStack stack) {
        return (spriteToMap, blockState) -> {
            String name = spriteToMap.getIconName();
            if ("texture_main".equals(name)) {
                return mainTextureLocation;
            }
            return new ResourceLocation("missingno");
        };
    }

    /**
     * @return The location of the main texture of the doorknob
     */
    public ResourceLocation getMainTextureLocation() {
        return mainTextureLocation;
    }

    /**
     * @return The name of the type of item
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return The material that the doorknob is made out of
     */
    public ToolMaterial getMaterial() {
        return material;
    }
}
