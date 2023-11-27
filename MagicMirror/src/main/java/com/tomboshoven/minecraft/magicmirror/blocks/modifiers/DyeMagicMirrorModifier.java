package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.DyeMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic mirror modifier that dyes the reflected entity.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DyeMagicMirrorModifier extends MagicMirrorModifier {
    @Override
    public String getName() {
        return "dye";
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
