package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.BannerMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;

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
    public boolean canModify(ItemStack heldItem, MagicMirrorCoreBlockEntity blockEntity) {
        // Must be activated using a banner.
        if (!(heldItem.getItem() instanceof BannerItem)) {
            return false;
        }

        // Must not have a banner modifier yet.
        return !hasModifierOfType(blockEntity);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(CompoundTag nbt, HolderLookup.Provider holderLookupProvider) {
        return new BannerMagicMirrorBlockEntityModifier(this, nbt, holderLookupProvider);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(ItemStack usedItem) {
        return new BannerMagicMirrorBlockEntityModifier(this, usedItem.split(1));
    }
}
