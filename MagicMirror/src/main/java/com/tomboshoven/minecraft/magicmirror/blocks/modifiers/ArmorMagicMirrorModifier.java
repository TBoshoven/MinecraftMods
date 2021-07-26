package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.ArmorMagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic mirror modifier that allows it to be used as an armor stand for switching an entire set of armor.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArmorMagicMirrorModifier extends MagicMirrorModifier {
    @Override
    public String getName() {
        return "armor";
    }

    @Override
    public boolean canModify(Level worldIn, BlockPos pos, ItemStack heldItem) {
        // Must be activated using an armor stand.
        if (heldItem.getItem() != Items.ARMOR_STAND) {
            return false;
        }

        // Must not have an armor modifier yet.
        return !hasModifierOfType(worldIn, pos);
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier(CompoundTag nbt) {
        MagicMirrorTileEntityModifier teModifier = new ArmorMagicMirrorTileEntityModifier(this);
        teModifier.read(nbt);
        return teModifier;
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier(ItemStack usedItem) {
        return new ArmorMagicMirrorTileEntityModifier(this);
    }
}
