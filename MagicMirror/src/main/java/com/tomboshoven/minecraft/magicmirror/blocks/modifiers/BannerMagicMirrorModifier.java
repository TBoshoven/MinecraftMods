package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.BannerMagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A magic mirror modifier that changes the mirror's background to a banner image.
 */
public class BannerMagicMirrorModifier extends MagicMirrorModifier {
    /**
     * The name of the modifier.
     * This should be stable as it gets written with save data.
     */
    public static String NAME = "banner";

    @Override
    public String getName() {
        return NAME;
    }

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
    MagicMirrorTileEntityModifier createTileEntityModifier(CompoundNBT nbt) {
        return new BannerMagicMirrorTileEntityModifier(this, nbt);
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier(ItemStack usedItem) {
        return new BannerMagicMirrorTileEntityModifier(this, usedItem.split(1));
    }
}
