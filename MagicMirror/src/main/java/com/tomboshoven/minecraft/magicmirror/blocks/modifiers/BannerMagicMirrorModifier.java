package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.BannerMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;

/**
 * A magic mirror modifier that changes the mirror's background to a banner image.
 */
public class BannerMagicMirrorModifier extends MagicMirrorModifier {
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
    MagicMirrorBlockEntityModifier createBlockEntityModifier(ValueInput input) {
        return new BannerMagicMirrorBlockEntityModifier(this, input);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(ItemStack usedItem) {
        return new BannerMagicMirrorBlockEntityModifier(this, usedItem.split(1));
    }
}
