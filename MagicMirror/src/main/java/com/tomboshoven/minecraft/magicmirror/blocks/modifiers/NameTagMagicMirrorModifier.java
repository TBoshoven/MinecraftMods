package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.NameTagMagicMirrorBlockEntityModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic mirror modifier that adds a name tag.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NameTagMagicMirrorModifier extends MagicMirrorModifier {
    @Override
    public String getName() {
        return "name_tag";
    }

    @Override
    public boolean canModify(ItemStack heldItem, MagicMirrorCoreBlockEntity blockEntity) {
        // Must be activated using a name tag.
        if (heldItem.getItem() != Items.NAME_TAG) {
            return false;
        }
        // Name tag must have a custom name.
        if (!heldItem.hasCustomHoverName()) {
            return false;
        }

        // Must not have an armor modifier yet.
        return !hasModifierOfType(blockEntity);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(CompoundTag nbt) {
        MagicMirrorBlockEntityModifier teModifier = new NameTagMagicMirrorBlockEntityModifier(this);
        teModifier.read(nbt);
        return teModifier;
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(ItemStack usedItem) {
        return new NameTagMagicMirrorBlockEntityModifier(this, usedItem.getHoverName());
    }
}
