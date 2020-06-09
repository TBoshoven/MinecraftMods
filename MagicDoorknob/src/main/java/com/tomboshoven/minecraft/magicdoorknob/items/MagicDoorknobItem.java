package com.tomboshoven.minecraft.magicdoorknob.items;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.MagicDoorTileEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.MagicDoorwayTileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic doorknob that allows you to open doors that don't exist.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicDoorknobItem extends Item {
    // The main material for rendering the item
    private final ResourceLocation mainTextureLocation;
    // The name of the type of item (used in NBT data; do not modify)
    private String typeName;
    // The item material, used for determining doorway generation properties
    private IItemTier tier;
    // The item tag to use in recipes
    private Tag<Item> recipeTag;

    /**
     * @param properties          The item properties
     * @param typeName            The main texture of the item
     * @param tier                The item material, used for determining doorway generation properties
     * @param mainTextureLocation The main material for rendering the block
     * @param recipeTag           The item tag to use in recipes
     */
    public MagicDoorknobItem(Item.Properties properties, String typeName, IItemTier tier, ResourceLocation mainTextureLocation, Tag<Item> recipeTag) {
        super(properties);

        this.typeName = typeName;
        this.tier = tier;
        this.mainTextureLocation = mainTextureLocation;
        this.recipeTag = recipeTag;
    }

    /**
     * @param world      The world to check
     * @param pos        The position to check
     * @param useContext The context for the interaction that triggered this check.
     * @return Whether the block can be replaced by a door or doorway
     */
    private static boolean isEmpty(IBlockReader world, BlockPos pos, BlockItemUseContext useContext) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock().isAir(blockState, world, pos)) {
            return true;
        }

        return blockState.isReplaceable(useContext);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            Direction face = context.getFace();
            BlockPos pos = context.getPos();

            // Only sideways doors right now
            if (face == Direction.UP || face == Direction.DOWN) {
                return ActionResultType.FAIL;
            }

            BlockItemUseContext useContext = new BlockItemUseContext(context);
            if (canPlaceDoor(world, pos, face, useContext)) {
                placeDoor(world, pos, face);
                placeDoorway(world, pos, face, useContext);
                context.getItem().shrink(1);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.FAIL;
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * Place a door at the given position.
     * This does not do any checks to see whether it's allowed.
     *
     * @param world  The world to place the door in
     * @param pos    The position of the top part of the door
     * @param facing The direction the door should be facing
     */
    private void placeDoor(World world, BlockPos pos, Direction facing) {
        BlockPos doorPos = pos.offset(facing);
        world.setBlockState(
                doorPos,
                Blocks.MAGIC_DOOR.getDefaultState()
                        .with(MagicDoorBlock.HORIZONTAL_FACING, facing)
                        .with(MagicDoorBlock.PART, MagicDoorBlock.EnumPartType.TOP)
        );
        TileEntity topTileEntity = world.getTileEntity(doorPos);
        if (topTileEntity instanceof MagicDoorTileEntity) {
            ((MagicDoorTileEntity) topTileEntity).setBaseBlockState(world.getBlockState(pos));
            ((MagicDoorTileEntity) topTileEntity).setDoorknob(this);
        }
        world.setBlockState(
                doorPos.down(),
                Blocks.MAGIC_DOOR.getDefaultState()
                        .with(MagicDoorBlock.HORIZONTAL_FACING, facing)
                        .with(MagicDoorBlock.PART, MagicDoorBlock.EnumPartType.BOTTOM)
        );
        TileEntity bottomTileEntity = world.getTileEntity(doorPos.down());
        if (bottomTileEntity instanceof MagicDoorTileEntity) {
            ((MagicDoorTileEntity) bottomTileEntity).setBaseBlockState(world.getBlockState(pos.down()));
            ((MagicDoorTileEntity) bottomTileEntity).setDoorknob(this);
        }
        world.playSound(null, doorPos, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
    }

    /**
     * Place a doorway that starts at the given position.
     * This does not do any checks to see whether it's allowed.
     *
     * @param world      The world to place the doorway in
     * @param pos        The position of the top part of the starting blocks of the doorway
     * @param facing     The direction the door is facing (outward from the doorway)
     * @param useContext The context for the interaction that triggered this check.
     */
    private void placeDoorway(World world, BlockPos pos, Direction facing, BlockItemUseContext useContext) {
        Direction doorwayFacing = facing.getOpposite();
        boolean isNorthSouth = facing == Direction.NORTH || facing == Direction.SOUTH;
        float depth = tier.getEfficiency();
        for (int i = 0; i < depth; ++i) {
            BlockPos elementPos = pos.offset(doorwayFacing, i);
            if (
                    (isReplaceable(world, elementPos) && !isEmpty(world, elementPos, useContext)) ||
                            (isReplaceable(world, elementPos.down()) && !isEmpty(world, elementPos.down(), useContext))
            ) {
                placeDoorwayElement(world, elementPos, isNorthSouth, MagicDoorwayBlock.EnumPartType.TOP);
                placeDoorwayElement(world, elementPos.down(), isNorthSouth, MagicDoorwayBlock.EnumPartType.BOTTOM);
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
    private void placeDoorwayElement(World world, BlockPos pos, boolean isNorthSouth, MagicDoorwayBlock.EnumPartType part) {
        if (isReplaceable(world, pos)) {
            BlockState state = world.getBlockState(pos);
            world.setBlockState(pos, Blocks.MAGIC_DOORWAY.getDefaultState().with(MagicDoorwayBlock.OPEN_NORTH_SOUTH, isNorthSouth).with(MagicDoorwayBlock.OPEN_EAST_WEST, !isNorthSouth).with(MagicDoorwayBlock.PART, part));

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof MagicDoorwayTileEntity) {
                ((MagicDoorwayTileEntity) tileEntity).setBaseBlockState(state);
                ((MagicDoorwayTileEntity) tileEntity).setDoorknob(this);
            }
        }
    }

    /**
     * Verify whether a door can be placed at the given position.
     *
     * @param world      The world to analyze
     * @param pos        The position in the world of the block that will be turned into a door
     * @param facing     The direction the door will be facing
     * @param useContext The context for the interaction that triggered this check.
     * @return Whether a door can be placed at the given position.
     */
    private boolean canPlaceDoor(IBlockReader world, BlockPos pos, Direction facing, BlockItemUseContext useContext) {
        if (!isReplaceable(world, pos) || !isReplaceable(world, pos.down())) {
            return false;
        }
        return isEmpty(world, pos.offset(facing), useContext) && isEmpty(world, pos.offset(facing).down(), useContext);
    }

    /**
     * Check whether this doorknob can replace the given block by a door or doorway.
     *
     * @param world The world to analyze
     * @param pos   The position to check
     * @return Whether this doorknob can replace the given block by a door or doorway
     */
    private boolean isReplaceable(IBlockReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasTileEntity()) {
            return false;
        }
        // Blocks like bedrock use this to prevent interactions
        if (blockState.getBlockHardness(world, pos) < 0) {
            return false;
        }
        return blockState.getHarvestLevel() <= tier.getHarvestLevel();
    }

    /**
     * @return The location of the main texture of the doorknob
     */
    @OnlyIn(Dist.CLIENT)
    public Material getMainMaterial() {
        return new Material(PlayerContainer.LOCATION_BLOCKS_TEXTURE, mainTextureLocation);
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
    public IItemTier getTier() {
        return tier;
    }

    /**
     * @return The item tag for use in recipes
     */
    public Tag<Item> getRecipeTag() {
        return recipeTag;
    }
}
