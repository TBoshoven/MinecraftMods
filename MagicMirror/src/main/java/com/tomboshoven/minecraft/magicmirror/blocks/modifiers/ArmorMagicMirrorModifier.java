package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.ArmorMagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A magic mirror modifier that allows it to be used as an armor stand for switching an entire set of armor.
 */
public class ArmorMagicMirrorModifier extends MagicMirrorModifier {
    /**
     * The name of the modifier.
     * This should be stable as it gets written with save data.
     */
    public static String NAME = "armor";

    @Override
    public String getName() {
        return NAME;
    }

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
    MagicMirrorTileEntityModifier createTileEntityModifier(CompoundNBT nbt) {
        return new ArmorMagicMirrorTileEntityModifier(this, nbt);
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier(ItemStack usedItem) {
        return new ArmorMagicMirrorTileEntityModifier(this, usedItem.split(1));
    }
}
