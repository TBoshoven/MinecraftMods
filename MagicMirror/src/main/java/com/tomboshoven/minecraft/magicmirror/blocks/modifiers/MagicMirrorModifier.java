package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A modifier for a magic mirror.
 * <p>
 * These are typically activated by right-clicking a mirror with an item.
 * In order to make a modifier available in the game, register it using the static register() method.
 */
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
     * @return The name of the modifier.
     */
    public abstract String getName();

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
     * @param blockEntity          The magic mirror block entity to apply the modifier to.
     * @param nbt                  The NBT tag to use for the modifier.
     * @param holderLookupProvider The lookup provider for resolving holders.
     */
    public void apply(MagicMirrorCoreBlockEntity blockEntity, CompoundTag nbt, HolderLookup.Provider holderLookupProvider) {
        MagicMirrorBlockEntityModifier magicMirrorBlockEntityModifier = createBlockEntityModifier(nbt, holderLookupProvider);
        blockEntity.addModifier(magicMirrorBlockEntityModifier);
    }

    /**
     * @param nbt                  The NBT tag of the modifier.
     * @param holderLookupProvider The lookup provider for resolving holders.
     * @return A new instance of the block entity modifier.
     */
    abstract MagicMirrorBlockEntityModifier createBlockEntityModifier(CompoundTag nbt, HolderLookup.Provider holderLookupProvider);

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
