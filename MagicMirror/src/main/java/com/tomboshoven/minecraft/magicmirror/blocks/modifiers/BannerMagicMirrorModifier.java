package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.BannerMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic mirror modifier that changes the mirror's background to a banner image.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerMagicMirrorModifier extends MagicMirrorModifier {
    @Override
    public String getName() {
        return "banner";
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
    MagicMirrorBlockEntityModifier createBlockEntityModifier(CompoundTag nbt) {
        MagicMirrorBlockEntityModifier teModifier = new BannerMagicMirrorBlockEntityModifier(this);
        teModifier.read(nbt);
        return teModifier;
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(ItemStack usedItem) {
        Item bannerType = usedItem.getItem();
        DyeColor bannerColor = bannerType instanceof BannerItem ? ((BannerItem) bannerType).getColor() : DyeColor.BLACK;
        CompoundTag bannerTag = usedItem.getTag();
        if (bannerTag != null) {
            // Get the block tag instead of the item stack tag
            bannerTag = bannerTag.getCompound("BlockEntityTag");
        }
        return new BannerMagicMirrorBlockEntityModifier(this, bannerColor, bannerTag, usedItem.hasCustomHoverName() ? usedItem.getHoverName() : null);
    }
}
