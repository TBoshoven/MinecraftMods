package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.BannerMagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
    public boolean canModify(Level worldIn, BlockPos pos, ItemStack heldItem) {
        // Must be activated using a banner.
        if (!(heldItem.getItem() instanceof BannerItem)) {
            return false;
        }

        // Must not have a banner modifier yet.
        return !hasModifierOfType(worldIn, pos);
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier(CompoundTag nbt) {
        MagicMirrorTileEntityModifier teModifier = new BannerMagicMirrorTileEntityModifier(this);
        teModifier.read(nbt);
        return teModifier;
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier(ItemStack usedItem) {
        Item bannerType = usedItem.getItem();
        DyeColor bannerColor = bannerType instanceof BannerItem ? ((BannerItem) bannerType).getColor() : DyeColor.BLACK;
        CompoundTag bannerTag = usedItem.getTag();
        if (bannerTag != null) {
            // Get the block tag instead of the item stack tag
            bannerTag = bannerTag.getCompound("BlockEntityTag");
        }
        return new BannerMagicMirrorTileEntityModifier(this, bannerColor, bannerTag, usedItem.hasCustomHoverName() ? usedItem.getHoverName() : null);
    }
}
