package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.BannerMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A magic mirror modifier that changes the mirror's background to a banner image.
 */
public class BannerMagicMirrorModifier extends MagicMirrorModifier {
    @Override
    public boolean canModify(World worldIn, BlockPos pos, ItemStack heldItem) {
        // Must be activated using a banner.
        if (!(heldItem.getItem() instanceof BannerItem)) {
            return false;
        }

        // Must not have a banner modifier yet.
        return !hasModifierOfType(worldIn, pos);
    }

    @Override
    MagicMirrorBlockEntityModifier createTileEntityModifier(CompoundNBT nbt) {
        return new BannerMagicMirrorBlockEntityModifier(this, nbt);
    }

    @Override
    MagicMirrorBlockEntityModifier createTileEntityModifier(ItemStack usedItem) {
        return new BannerMagicMirrorBlockEntityModifier(this, usedItem.split(1));
    }
}
