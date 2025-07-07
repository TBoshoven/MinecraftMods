package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.storage.ValueInput;

import javax.annotation.Nullable;

public class BannerMagicMirrorBlockEntityModifier extends ItemBasedMagicMirrorBlockEntityModifier {
    public BannerMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier, item);
    }

    public BannerMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ValueInput input) {
        super(modifier, input);
    }

    /**
     * @return The base color of the banner.
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
     * @return The patterns to use when rendering this banner.
     */
    @Nullable
    public BannerPatternLayers getPatterns() {
        return item.get(DataComponents.BANNER_PATTERNS);
    }
}
