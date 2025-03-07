package com.tomboshoven.minecraft.magicdoorknob.items;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.function.Supplier;

/**
 * A magic doorknob that allows you to open doors that don't exist.
 */
public class MagicDoorknobItem extends Item {
    // The main material for rendering the item
    private final ResourceLocation mainTextureLocation;
    // The name of the type of item (used in NBT data; do not modify)
    private final String typeName;
    // The item material, used for determining doorway generation properties
    private final IItemTier tier;
    // The ingredient used to make doorknobs of this type
    private final Supplier<Ingredient> ingredient;
    /**
     * The maximum allowed length of a doorway.
     */
    public static final int MAX_DOORWAY_LENGTH = 128;

    /**
     * @param properties          The item properties
     * @param typeName            The main texture of the item
     * @param tier                The item material, used for determining doorway generation properties
     * @param mainTextureLocation The main material for rendering the block
     * @param ingredient          The ingredient used to make doorknobs of this type
     */
    MagicDoorknobItem(Item.Properties properties, String typeName, IItemTier tier, ResourceLocation mainTextureLocation, Supplier<Ingredient> ingredient) {
        super(properties);

        this.typeName = typeName;
        this.tier = tier;
        this.mainTextureLocation = mainTextureLocation;
        this.ingredient = ingredient;
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

        return blockState.canBeReplaced(useContext);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (!world.isClientSide) {
            Direction face = context.getClickedFace();
            BlockPos pos = context.getClickedPos();

            // Only sideways doors right now
            if (face == Direction.UP || face == Direction.DOWN) {
                return ActionResultType.FAIL;
            }

            BlockItemUseContext useContext = new BlockItemUseContext(context);
            if (canPlaceDoor(world, pos, face, useContext)) {
                placeDoor(world, pos, face);
                placeDoorway(world, pos, face, useContext);
                context.getItemInHand().shrink(1);
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
        BlockPos doorPos = pos.relative(facing);
        Block block = Blocks.MAGIC_DOOR.get();
        world.setBlockAndUpdate(
                doorPos,
                block.defaultBlockState()
                        .setValue(MagicDoorBlock.HORIZONTAL_FACING, facing)
                        .setValue(MagicDoorBlock.PART, MagicDoorBlock.EnumPartType.TOP)
        );
        TileEntity topBlockEntity = world.getBlockEntity(doorPos);
        if (topBlockEntity instanceof MagicDoorBlockEntity) {
            MagicDoorBlockEntity topDoorBlockEntity = (MagicDoorBlockEntity) topBlockEntity;
            BlockState blockState = world.getBlockState(pos);
            if (blockState.getBlock() == Blocks.MAGIC_DOORWAY.get()) {
                TileEntity targetBlockEntity = world.getBlockEntity(pos);
                if (targetBlockEntity instanceof MagicDoorwayBlockEntity) {
                    topDoorBlockEntity.setBaseBlockState(((MagicDoorwayBlockEntity)targetBlockEntity).getBaseBlockState());
                }
            }
            else {
                topDoorBlockEntity.setBaseBlockState(blockState);
            }
            topDoorBlockEntity.setDoorknob(this);
        }
        world.setBlockAndUpdate(
                doorPos.below(),
                block.defaultBlockState()
                        .setValue(MagicDoorBlock.HORIZONTAL_FACING, facing)
                        .setValue(MagicDoorBlock.PART, MagicDoorBlock.EnumPartType.BOTTOM)
        );
        TileEntity bottomBlockEntity = world.getBlockEntity(doorPos.below());
        if (bottomBlockEntity instanceof MagicDoorBlockEntity) {
            MagicDoorBlockEntity bottomDoorBlockEntity = (MagicDoorBlockEntity) bottomBlockEntity;
            BlockState blockState = world.getBlockState(pos.below());
            if (blockState.getBlock() == Blocks.MAGIC_DOORWAY.get()) {
                TileEntity targetBlockEntity = world.getBlockEntity(pos.below());
                if (targetBlockEntity instanceof MagicDoorwayBlockEntity) {
                    bottomDoorBlockEntity.setBaseBlockState(((MagicDoorwayBlockEntity)targetBlockEntity).getBaseBlockState());
                }
            }
            else {
                bottomDoorBlockEntity.setBaseBlockState(blockState);
            }
            bottomDoorBlockEntity.setDoorknob(this);
        }
        world.playSound(null, doorPos, SoundEvents.WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
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
        double depth = getDepth();
        for (int i = 0; i < depth; ++i) {
            BlockPos elementPos = pos.relative(doorwayFacing, i);
            if (
                    (isReplaceable(world, elementPos) && !isEmpty(world, elementPos, useContext)) ||
                            (isReplaceable(world, elementPos.below()) && !isEmpty(world, elementPos.below(), useContext))
            ) {
                placeDoorwayElement(world, elementPos, isNorthSouth, MagicDoorwayBlock.EnumPartType.TOP);
                placeDoorwayElement(world, elementPos.below(), isNorthSouth, MagicDoorwayBlock.EnumPartType.BOTTOM);
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
            Block block = Blocks.MAGIC_DOORWAY.get();
            if (state.getBlock() == block) {
                BlockState newState = state.setValue(isNorthSouth ? MagicDoorwayBlock.OPEN_NORTH_SOUTH : MagicDoorwayBlock.OPEN_EAST_WEST, true);
                if (part == MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM && state.getValue(MagicDoorwayBlock.PART) == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
                    newState = newState.setValue(MagicDoorwayBlock.PART, MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM).setValue(MagicDoorwayBlock.OPEN_CROSS_TOP_BOTTOM, true);
                }
                world.setBlockAndUpdate(pos, newState);
            }
            else {
                world.setBlockAndUpdate(pos, block.defaultBlockState().setValue(MagicDoorwayBlock.OPEN_NORTH_SOUTH, isNorthSouth).setValue(MagicDoorwayBlock.OPEN_EAST_WEST, !isNorthSouth).setValue(MagicDoorwayBlock.PART, part));

                TileEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof MagicDoorwayBlockEntity) {
                    ((MagicDoorwayPartBaseBlockEntity) tileEntity).setBaseBlockState(state);
                    ((MagicDoorwayPartBaseBlockEntity) tileEntity).setDoorknob(this);
                }
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
        if (!isReplaceable(world, pos) || !isReplaceable(world, pos.below())) {
            return false;
        }
        return isEmpty(world, pos.relative(facing), useContext) && isEmpty(world, pos.relative(facing).below(), useContext);
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
        if (blockState.getBlock() == Blocks.MAGIC_DOORWAY.get()) {
            return true;
        }
        if (blockState.hasTileEntity()) {
            return false;
        }
        // Blocks like bedrock use this to prevent interactions
        if (blockState.getDestroySpeed(world, pos) < 0) {
            return false;
        }
        return blockState.getHarvestLevel() <= tier.getLevel();
    }

    /**
     * @return The location of the main texture of the doorknob
     */
    public Material getMainMaterial() {
        return new Material(PlayerContainer.BLOCK_ATLAS, mainTextureLocation);
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
     * @return The maximum size of the doorway created by this doorknob
     */
    public double getDepth() {
        return Math.min(getTier().getSpeed() * Config.SERVER.doorwayMultiplier.get(), MAX_DOORWAY_LENGTH);
    }

    /**
     * @return The ingredient used to make doorknobs of this type
     */
    public Ingredient getIngredient() {
        return ingredient.get();
    }
}
