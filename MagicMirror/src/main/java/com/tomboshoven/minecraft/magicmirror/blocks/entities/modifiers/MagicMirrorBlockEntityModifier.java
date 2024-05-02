package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A magic mirror modifier as applied to a block entity.
 * Instead of using this directly, apply it using a MagicMirrorModifier instance.
 */
public abstract class MagicMirrorBlockEntityModifier {
    /**
     * The modifier that applied this object to the block entity.
     */
    private final MagicMirrorModifier modifier;

    /**
     * The number of ticks left in the cooldown.
     */
    private int cooldown;

    /**
     * @param modifier The modifier that applied this object to the block entity.
     */
    MagicMirrorBlockEntityModifier(MagicMirrorModifier modifier) {
        this.modifier = modifier;
    }

    /**
     * Write the modifier out to an NBT tag compound.
     * Including the name of the modifier is not necessary.
     *
     * @param nbt The NBT tag compound to write to.
     * @return The input compound, for chaining.
     */
    public CompoundTag write(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        return nbt;
    }

    /**
     * Called when the block entity is removed.
     * Can be used for things like spawning contained items back into the world.
     *
     * @param world The world that the block entity is being removed from.
     * @param pos   The position of the removed block in the world.
     */
    public abstract void remove(Level world, BlockPos pos);

    /**
     * Called when the modifier is attached to the block entity.
     *
     * @param blockEntity The block entity that is being modified.
     */
    public void activate(MagicMirrorCoreBlockEntity blockEntity) {
    }

    /**
     * Called when the modifier is detached from the block entity.
     *
     * @param blockEntity The block entity that is being modified.
     */
    public void deactivate(MagicMirrorCoreBlockEntity blockEntity) {
    }

    /**
     * Called when the player activates a magic mirror that is modified by this modifier.
     *
     * @param blockEntity The block entity of the magic mirror that is being activated.
     * @param player      The player that is activating the magic mirror.
     * @return Whether it was activated. If true, no other modifiers are evaluated.
     */
    public InteractionResult useWithoutItem(MagicMirrorCoreBlockEntity blockEntity, Player player) {
        return InteractionResult.PASS;
    }

    /**
     * Called when the player activates a magic mirror that is modified by this modifier.
     *
     * @param blockEntity The block entity of the magic mirror that is being activated.
     * @param player      The player that is activating the magic mirror.
     * @param heldItem    The item held by the player while activating the mirror.
     * @return Whether it was activated. If true, no other modifiers are evaluated.
     */
    public ItemInteractionResult useWithItem(MagicMirrorCoreBlockEntity blockEntity, Player player, ItemStack heldItem) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * When the modifier is used, this can be used to easily cool down, so it can't be activated all the time.
     *
     * @param ticks The number of ticks to wait before being cooled down completely.
     */
    void setCooldown(int ticks) {
        cooldown = ticks;
    }

    /**
     * @return Whether the modifier is cooling down.
     */
    boolean coolingDown() {
        return cooldown > 0;
    }

    /**
     * Cool down the modifier if necessary.
     */
    public void coolDown() {
        if (cooldown > 0) {
            --cooldown;
        }
    }

    /**
     * @return The modifier that applied this object to the block entity.
     */
    public MagicMirrorModifier getModifier() {
        return modifier;
    }
}
