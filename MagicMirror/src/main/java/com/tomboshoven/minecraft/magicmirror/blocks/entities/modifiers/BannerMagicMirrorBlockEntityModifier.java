package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import javax.annotation.Nullable;

public class BannerMagicMirrorBlockEntityModifier extends ItemBasedMagicMirrorBlockEntityModifier {
    public BannerMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier, item);
    }

    public BannerMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        super(modifier, nbt, lookupProvider);
    }

    @Override
    protected ItemStack getItemStackOldNbt(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        DyeColor baseColor = DyeColor.byId(nbt.getInt("BannerColor"));
        CompoundTag bannerData = nbt.getCompound("BannerData");
        Component name = Component.Serializer.fromJson(nbt.getString("BannerName"), lookupProvider);

        Block block = BannerBlock.byColor(baseColor);
        ItemStack itemStack = new ItemStack(block);
        itemStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(bannerData));
        if (name != null) {
            itemStack.set(DataComponents.CUSTOM_NAME, name);
        }
        return itemStack;
    }

    /**
     * @return the base color of the banner.
     */
    @Nullable
    public DyeColor getBaseColor() {
        if (item.getItem() instanceof BannerItem bannerItem) {
            return bannerItem.getColor();
        }
        return null;
    }

    /**
     * Get a copy of the pattern layers for this banner.
     * Does not include the base color.
     *
     * @return the patterns to use when rendering this banner.
     */
    @Nullable
    public BannerPatternLayers getPatterns() {
        return item.get(DataComponents.BANNER_PATTERNS);
    }
}
