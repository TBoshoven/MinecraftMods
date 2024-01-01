package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.DyeMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

/**
 * A magic mirror modifier that dyes the reflected entity.
 */
public class DyeMagicMirrorModifier extends MagicMirrorModifier {
    /**
     * The name of the modifier.
     * This should be stable as it gets written with save data.
     */
    public static final String NAME = "dye";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canModify(ItemStack heldItem, MagicMirrorCoreBlockEntity blockEntity) {
        // Must be activated using a dye item.
        if (!(heldItem.getItem() instanceof DyeItem)) {
            return false;
        }

        // Must not have a dye modifier yet.
        return !hasModifierOfType(blockEntity);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(CompoundTag nbt) {
        return new DyeMagicMirrorBlockEntityModifier(this, nbt);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(ItemStack usedItem) {
        return new DyeMagicMirrorBlockEntityModifier(this, usedItem);
    }
}
