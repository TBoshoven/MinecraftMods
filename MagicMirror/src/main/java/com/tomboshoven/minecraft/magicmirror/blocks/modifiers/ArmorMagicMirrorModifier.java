package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * A magic mirror modifier that allows it to be used as an armor stand for switching an entire set of armor.
 */
public class ArmorMagicMirrorModifier extends MagicMirrorModifier {
    @Override
    public boolean canModify(ItemStack heldItem, MagicMirrorCoreBlockEntity blockEntity) {
        // Must be activated using an armor stand.
        if (heldItem.getItem() != Items.ARMOR_STAND) {
            return false;
        }

        // Must not have an armor modifier yet.
        return !hasModifierOfType(blockEntity);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(CompoundTag nbt, HolderLookup.Provider holderLookupProvider) {
        return new ArmorMagicMirrorBlockEntityModifier(this, nbt, holderLookupProvider);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(ItemStack usedItem) {
        return new ArmorMagicMirrorBlockEntityModifier(this, usedItem.split(1));
    }
}
