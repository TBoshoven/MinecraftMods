package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A magic mirror modifier that allows it to be used as an armor stand for switching an entire set of armor.
 */
public class ArmorMagicMirrorModifier extends MagicMirrorModifier {
    @Override
    public boolean canModify(World worldIn, BlockPos pos, ItemStack heldItem) {
        // Must be activated using an armor stand.
        if (heldItem.getItem() != Items.ARMOR_STAND) {
            return false;
        }

        // Must not have an armor modifier yet.
        return !hasModifierOfType(worldIn, pos);
    }

    @Override
    MagicMirrorBlockEntityModifier createTileEntityModifier(CompoundNBT nbt) {
        return new ArmorMagicMirrorBlockEntityModifier(this, nbt);
    }

    @Override
    MagicMirrorBlockEntityModifier createTileEntityModifier(ItemStack usedItem) {
        return new ArmorMagicMirrorBlockEntityModifier(this, usedItem.split(1));
    }
}
