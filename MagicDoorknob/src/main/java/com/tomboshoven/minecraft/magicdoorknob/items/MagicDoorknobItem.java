package com.tomboshoven.minecraft.magicdoorknob.items;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.config.Config;
import com.tomboshoven.minecraft.magicdoorknob.enchantments.Enchantments;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;

import javax.annotation.Nullable;
import java.util.Optional;
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
    private final Tier tier;
    // The ingredient used to make doorknobs of this type
    @Nullable
    private final Supplier<Ingredient> craftingIngredient;
    @Nullable
    private final Item netheriteSmithingBase;

    /**
     * The maximum allowed length of a doorway.
     */
    public static final int MAX_DOORWAY_LENGTH = 128;

    /**
     * @param properties            The item properties
     * @param typeName              The main texture of the item
     * @param tier                  The item material, used for determining doorway generation properties
     * @param mainTextureLocation   The main material for rendering the block
     * @param craftingIngredient    The ingredient used to make doorknobs of this type
     * @param netheriteSmithingBase The base item for use with netherite smithing
     */
    MagicDoorknobItem(Item.Properties properties, String typeName, Tier tier, ResourceLocation mainTextureLocation, @Nullable Supplier<Ingredient> craftingIngredient, @Nullable Item netheriteSmithingBase) {
        super(properties);

        this.typeName = typeName;
        this.tier = tier;
        this.mainTextureLocation = mainTextureLocation;
        this.craftingIngredient = craftingIngredient;
        this.netheriteSmithingBase = netheriteSmithingBase;
    }

    /**
     * @param world      The world to check
     * @param pos        The position to check
     * @param useContext The context for the interaction that triggered this check.
     * @return Whether the block can be replaced by a door or doorway
     */
    private static boolean isEmpty(BlockGetter world, BlockPos pos, BlockPlaceContext useContext) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || blockState.canBeReplaced(useContext);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            Direction face = context.getClickedFace();
            BlockPos pos = context.getClickedPos();

            // Only sideways doors right now
            if (face == Direction.UP || face == Direction.DOWN) {
                return InteractionResult.FAIL;
            }

            BlockPlaceContext useContext = new BlockPlaceContext(context);
            if (canPlaceDoor(world, pos, face, useContext)) {
                int doorwayLength = placeDoorway(world, pos, face, useContext);
                if (doorwayLength > 0) {
                    placeDoor(world, pos, face, context.getItemInHand().split(1), doorwayLength);
                    Direction oppositeFace = face.getOpposite();
                    BlockPos otherDoorwayPos = pos.relative(oppositeFace, doorwayLength - 1);

                    Player player = context.getPlayer();
                    boolean doubleDoor = player != null && EnchantmentHelper.getEnchantmentLevel(Enchantments.DOUBLE.get(), player) > 0;

                    if (doubleDoor && canPlaceDoor(world, otherDoorwayPos, oppositeFace, useContext)) {
                        placeDoor(world, otherDoorwayPos, oppositeFace, ItemStack.EMPTY, doorwayLength);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Place a door at the given position.
     * This does not do any checks to see whether it's allowed.
     *
     * @param world         The world to place the door in
     * @param pos           The position of the top part of the door
     * @param facing        The direction the door should be facing
     * @param doorknob      The doorknob to attach to the door
     * @param doorwayLength The length of the doorway (used when closing)
     */
    private void placeDoor(Level world, BlockPos pos, Direction facing, ItemStack doorknob, int doorwayLength) {
        BlockPos doorPos = pos.relative(facing);
        Block block = Blocks.MAGIC_DOOR.get();
        world.setBlockAndUpdate(
                doorPos,
                block.defaultBlockState()
                        .setValue(MagicDoorBlock.HORIZONTAL_FACING, facing)
                        .setValue(MagicDoorBlock.PART, MagicDoorBlock.EnumPartType.TOP)
        );
        BlockEntity topBlockEntity = world.getBlockEntity(doorPos);
        if (topBlockEntity instanceof MagicDoorBlockEntity topDoorBlockEntity) {
            topDoorBlockEntity.setDoorknobItem(doorknob);
            BlockState blockState = world.getBlockState(pos);
            if (blockState.is(Blocks.MAGIC_DOORWAY.get())) {
                if (world.getBlockEntity(pos) instanceof MagicDoorwayBlockEntity magicDoorwayBlockEntity) {
                    topDoorBlockEntity.setBaseBlockState(magicDoorwayBlockEntity.getBaseBlockState());
                }
            }
            else {
                topDoorBlockEntity.setBaseBlockState(blockState);
            }
            topDoorBlockEntity.setDoorknob(this);
            topDoorBlockEntity.setDoorwayLength(doorwayLength);
        }
        world.setBlockAndUpdate(
                doorPos.below(),
                block.defaultBlockState()
                        .setValue(MagicDoorBlock.HORIZONTAL_FACING, facing)
                        .setValue(MagicDoorBlock.PART, MagicDoorBlock.EnumPartType.BOTTOM)
        );
        BlockEntity bottomBlockEntity = world.getBlockEntity(doorPos.below());
        if (bottomBlockEntity instanceof MagicDoorBlockEntity bottomDoorBlockEntity) {
            BlockState blockState = world.getBlockState(pos.below());
            if (blockState.is(Blocks.MAGIC_DOORWAY.get())) {
                if (world.getBlockEntity(pos.below()) instanceof MagicDoorwayBlockEntity magicDoorwayBlockEntity) {
                    bottomDoorBlockEntity.setBaseBlockState(magicDoorwayBlockEntity.getBaseBlockState());
                }
            }
            else {
                bottomDoorBlockEntity.setBaseBlockState(blockState);
            }
            bottomDoorBlockEntity.setDoorknob(this);
            bottomDoorBlockEntity.setDoorwayLength(doorwayLength);
        }
        world.playSound(null, doorPos, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 1, 1);
    }

    /**
     * Place a doorway that starts at the given position.
     * This does not do any checks to see whether it's allowed.
     *
     * @param world      The world to place the doorway in
     * @param pos        The position of the top part of the starting blocks of the doorway
     * @param facing     The direction the door is facing (outward from the doorway)
     * @param useContext The context for the interaction that triggered this check.
     * @return The length of the doorway
     */
    private int placeDoorway(Level world, BlockPos pos, Direction facing, BlockPlaceContext useContext) {
        Direction doorwayFacing = facing.getOpposite();
        boolean isNorthSouth = facing == Direction.NORTH || facing == Direction.SOUTH;
        Player player = useContext.getPlayer();
        int efficiency = player == null ? 0 : EnchantmentHelper.getBlockEfficiency(player);
        double depth = getDepth(efficiency);
        int i;
        for (i = 0; i < depth; ++i) {
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
        return i;
    }

    /**
     * Place a single doorway element.
     *
     * @param world        The world to place the doorway in
     * @param pos          The position of the top part of the starting blocks of the doorway
     * @param isNorthSouth Whether this is a north-south-facing doorway (as opposed to east-west)
     * @param part         Whether this is the top or bottom part
     */
    private void placeDoorwayElement(Level world, BlockPos pos, boolean isNorthSouth, MagicDoorwayBlock.EnumPartType part) {
        if (isReplaceable(world, pos)) {
            BlockState state = world.getBlockState(pos);
            Block block = Blocks.MAGIC_DOORWAY.get();
            if (state.is(block)) {
                BlockState newState = state.setValue(isNorthSouth ? MagicDoorwayBlock.OPEN_NORTH_SOUTH : MagicDoorwayBlock.OPEN_EAST_WEST, true);
                if (part == MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM && state.getValue(MagicDoorwayBlock.PART) == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
                    newState = newState.setValue(MagicDoorwayBlock.PART, MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM).setValue(MagicDoorwayBlock.OPEN_CROSS_TOP_BOTTOM, true);
                }
                world.setBlockAndUpdate(pos, newState);
            }
            else {
                world.setBlockAndUpdate(pos, block.defaultBlockState().setValue(MagicDoorwayBlock.OPEN_NORTH_SOUTH, isNorthSouth).setValue(MagicDoorwayBlock.OPEN_EAST_WEST, !isNorthSouth).setValue(MagicDoorwayBlock.PART, part));

                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof MagicDoorwayBlockEntity) {
                    ((MagicDoorwayPartBaseBlockEntity) blockEntity).setBaseBlockState(state);
                    ((MagicDoorwayPartBaseBlockEntity) blockEntity).setDoorknob(this);
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
    private boolean canPlaceDoor(BlockGetter world, BlockPos pos, Direction facing, BlockPlaceContext useContext) {
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
    private boolean isReplaceable(BlockGetter world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.is(Blocks.MAGIC_DOORWAY.get())) {
            return true;
        }
        if (blockState.hasBlockEntity()) {
            return false;
        }
        // Blocks like bedrock use this to prevent interactions
        if (blockState.getDestroySpeed(world, pos) < 0) {
            return false;
        }
        return TierSortingRegistry.isCorrectTierForDrops(tier, blockState);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.DOUBLE.get() || enchantment == net.minecraft.world.item.enchantment.Enchantments.BLOCK_EFFICIENCY;
    }

    /**
     * @return The location of the main texture of the doorknob
     */
    public Material getMainMaterial() {
        return new Material(InventoryMenu.BLOCK_ATLAS, mainTextureLocation);
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
    public Tier getTier() {
        return tier;
    }

    /**
     * @return The maximum size of the doorway created by this doorknob
     */
    public double getDepth(double efficiency) {
        return Math.min((getTier().getSpeed() + efficiency) * Config.SERVER.doorwayMultiplier.get(), MAX_DOORWAY_LENGTH);
    }

    /**
     * @return The ingredient used to make doorknobs of this type
     */
    public @Nullable Ingredient getCraftingIngredient() {
        return Optional.ofNullable(craftingIngredient).map(Supplier::get).orElse(null);
    }

    /**
     * @return The base item to use for netherite smithing into this item
     */
    public @Nullable Item getNetheriteSmithingBase() {
        return netheriteSmithingBase;
    }
}
