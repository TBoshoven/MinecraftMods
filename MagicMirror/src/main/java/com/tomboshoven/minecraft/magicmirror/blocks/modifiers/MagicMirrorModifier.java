package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A modifier for a magic mirror.
 * <p>
 * These are typically activated by right-clicking a mirror with an item.
 * In order to make a modifier available in the game, register it using the static register() method.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MagicMirrorModifier {
    /**
     * A registry of all modifiers.
     */
    private static final Map<String, MagicMirrorModifier> MODIFIERS = Maps.newHashMap();

    /**
     * Register a new modifier into the game.
     *
     * @param modifier The modifier to be registered. Must have a unique name.
     */
    public static void register(MagicMirrorModifier modifier) {
        MODIFIERS.putIfAbsent(modifier.getName(), modifier);
    }

    /**
     * @return A collection of all modifiers that are registered in the game.
     */
    public static Collection<MagicMirrorModifier> getModifiers() {
        return Collections.unmodifiableCollection(MODIFIERS.values());
    }

    /**
     * Get a modifier by name, or null if no such modifier exists.
     *
     * @param name The name of the requested modifier.
     * @return A modifier with the provided name, or null if it does not exist.
     */
    @Nullable
    public static MagicMirrorModifier getModifier(String name) {
        return MODIFIERS.get(name);
    }

    /**
     * Helper method to get a magic mirror block entity at the given position.
     *
     * @param worldIn The world containing the magic mirror.
     * @param pos     The position in the world of the block.
     * @return The block entity, or null if it does not exist or is not a magic mirror block entity.
     */
    @Nullable
    private static MagicMirrorCoreBlockEntity getMagicMirrorBlockEntity(BlockGetter worldIn, BlockPos pos) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof MagicMirrorCoreBlockEntity) {
            return (MagicMirrorCoreBlockEntity) blockEntity;
        }
        return null;
    }

    /**
     * Find out whether the mirror at the given position already has a modifier of a given type.
     *
     * @param world    The world in which to check the block.
     * @param pos      The position of the mirror block to check.
     * @param modifier The modifier to test for.
     * @return Whether the mirror at the given position has the given modifier.
     */
    private static boolean hasModifierOfType(BlockGetter world, BlockPos pos, MagicMirrorModifier modifier) {
        MagicMirrorCoreBlockEntity magicMirrorBlockEntity = getMagicMirrorBlockEntity(world, pos);
        if (magicMirrorBlockEntity == null) {
            return false;
        }
        return magicMirrorBlockEntity.getModifiers().stream().anyMatch(teModifier -> teModifier.getModifier() == modifier);
    }

    /**
     * @return The name of the modifier.
     */
    public abstract String getName();

    /**
     * Whether the modifier can be applied when the player uses the held item on the magic mirror block.
     * <p>
     * This should also check constraints such as not modifying the same block twice etc.
     *
     * @param worldIn  The world containing the magic mirror.
     * @param pos      The position in the world of the block.
     * @param heldItem The item used on the block.
     * @return Whether the block can be modified in the given configuration.
     */
    public abstract boolean canModify(Level worldIn, BlockPos pos, ItemStack heldItem);

    /**
     * Apply the modifier to the magic mirror.
     * <p>
     * This assumes that canModify(...) is true.
     *
     * @param worldIn  The world containing the magic mirror.
     * @param pos      The position in the world of the block.
     * @param heldItem The item used on the block.
     */
    public void apply(BlockGetter worldIn, BlockPos pos, ItemStack heldItem) {
        MagicMirrorCoreBlockEntity magicMirrorBlockEntity = getMagicMirrorBlockEntity(worldIn, pos);
        if (magicMirrorBlockEntity == null) {
            return;
        }
        apply(magicMirrorBlockEntity, heldItem);
    }

    /**
     * Apply the modifier to the magic mirror.
     * <p>
     * This assumes that canModify(...) is true.
     *
     * @param blockEntity The magic mirror block entity to apply the modifier to.
     * @param heldItem    The item used on the block.
     */
    private void apply(MagicMirrorCoreBlockEntity blockEntity, ItemStack heldItem) {
        blockEntity.addModifier(createBlockEntityModifier(heldItem));
        heldItem.shrink(1);
    }

    /**
     * Apply the modifier to the magic mirror as specified in an NBT tag.
     *
     * @param blockEntity The magic mirror block entity to apply the modifier to.
     * @param nbt         The NBT tag to use for the modifier.
     */
    public void apply(MagicMirrorCoreBlockEntity blockEntity, CompoundTag nbt) {
        MagicMirrorBlockEntityModifier magicMirrorBlockEntityModifier = createBlockEntityModifier(nbt);
        blockEntity.addModifier(magicMirrorBlockEntityModifier);
    }

    /**
     * @param nbt The NBT tag of the modifier.
     * @return A new instance of the block entity modifier.
     */
    abstract MagicMirrorBlockEntityModifier createBlockEntityModifier(CompoundTag nbt);

    /**
     * @param usedItem The item used to attach the modifier.
     * @return A new instance of the block entity modifier.
     */
    abstract MagicMirrorBlockEntityModifier createBlockEntityModifier(ItemStack usedItem);

    /**
     * Find out whether the mirror at the given position already has a modifier of the current type.
     *
     * @param world The world in which to check the block.
     * @param pos   The position of the mirror block to check.
     * @return Whether the mirror at the given position has the current modifier.
     */
    boolean hasModifierOfType(BlockGetter world, BlockPos pos) {
        return hasModifierOfType(world, pos, this);
    }
}
