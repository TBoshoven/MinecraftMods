package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.BannerMagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        MagicMirrorTileEntityModifier teModifier = new BannerMagicMirrorTileEntityModifier(this);
        teModifier.read(nbt);
        return teModifier;
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier(ItemStack usedItem) {
        Item bannerType = usedItem.getItem();
        DyeColor bannerColor = bannerType instanceof BannerItem ? ((BannerItem) bannerType).getColor() : DyeColor.BLACK;
        CompoundNBT bannerTag = usedItem.getTag();
        if (bannerTag != null) {
            // Get the block tag instead of the item stack tag
            bannerTag = bannerTag.getCompound("BlockEntityTag");
        }
        return new BannerMagicMirrorTileEntityModifier(this, bannerColor, bannerTag, usedItem.hasCustomHoverName() ? usedItem.getHoverName() : null);
    }
}
