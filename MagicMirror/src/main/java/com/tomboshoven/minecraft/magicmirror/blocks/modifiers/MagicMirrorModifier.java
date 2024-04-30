package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * A modifier for a magic mirror.
 * <p>
 * These are typically activated by right-clicking a mirror with an item.
 * In order to make a modifier available in the game, register it using the static register() method.
 */
public abstract class MagicMirrorModifier {
    /**
     * Find out whether the mirror at the given position already has a modifier of a given type.
     *
     * @param blockEntity The block entity of the mirror to check.
     * @param modifier The modifier to test for.
     * @return Whether the mirror at the given position has the given modifier.
     */
    private static boolean hasModifierOfType(MagicMirrorCoreBlockEntity blockEntity, MagicMirrorModifier modifier) {
        return blockEntity.getModifiers().stream().anyMatch(teModifier -> teModifier.getModifier() == modifier);
    }

    /**
     * Whether the modifier can be applied when the player uses the held item on the magic mirror block.
     * <p>
     * This should also check constraints such as not modifying the same block twice etc.
     *
     * @param heldItem The item used on the block.
     * @param blockEntity The block entity of the mirror to check.
     * @return Whether the block can be modified in the given configuration.
     */
    public abstract boolean canModify(ItemStack heldItem, MagicMirrorCoreBlockEntity blockEntity);

    /**
     * Apply the modifier to the magic mirror.
     * <p>
     * This assumes that canModify(...) is true.
     *
     * @param blockEntity The magic mirror block entity to apply the modifier to.
     * @param heldItem    The item used on the block.
     */
    public void apply(MagicMirrorCoreBlockEntity blockEntity, ItemStack heldItem) {
        blockEntity.addModifier(createBlockEntityModifier(heldItem));
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
     * @param blockEntity The block entity of the mirror to check.
     * @return Whether the mirror at the given position has the current modifier.
     */
    boolean hasModifierOfType(MagicMirrorCoreBlockEntity blockEntity) {
        return hasModifierOfType(blockEntity, this);
    }
}
