package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class BannerMagicMirrorTileEntityModifier extends ItemBasedMagicMirrorTileEntityModifier {
    public BannerMagicMirrorTileEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier, item);
    }

    public BannerMagicMirrorTileEntityModifier(MagicMirrorModifier modifier, CompoundNBT nbt) {
        super(modifier, nbt);
    }

    @Override
    protected ItemStack getItemStackOldNbt(CompoundNBT nbt) {
        DyeColor baseColor = DyeColor.byId(nbt.getInt("BannerColor"));
        CompoundNBT bannerData = nbt.getCompound("BannerData");
        ITextComponent name = ITextComponent.Serializer.fromJson(nbt.getString("BannerName"));

        // BannerBlock.forColor is client-only.
        // Let's do a super-ugly workaround.
        // This will cause issues if dye colors are ever made extensible.
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(String.format("minecraft:%s_banner", baseColor.getName())));
        if (block == null) {
            return super.getItemStackOldNbt(nbt);
        }
        ItemStack itemStack = new ItemStack(block);
        itemStack.getOrCreateTag().put("BlockEntityTag", bannerData);
        if (name != null) {
            itemStack.setHoverName(name);
        }
        return itemStack;
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorBaseTileEntity blockEntity, PlayerEntity playerIn, Hand hand) {
        // No activation behavior
        return false;
    }

    /**
     * Get a copy of the "pattern list" for this banner.
     * This list contains a series of patterns and the colors to draw them in.
     *
     * @return the pattern list to use when rendering this banner.
     */
    @Nullable
    public List<Pair<BannerPattern, DyeColor>> getPatternList() {
        if (item.getItem() instanceof BannerItem) {
            BannerItem bannerItem = (BannerItem) item.getItem();
            DyeColor baseColor = bannerItem.getColor();
            CompoundNBT bannerNBT = item.getTagElement("BlockEntityTag");

            List<Pair<BannerPattern, DyeColor>> patternList = Lists.newArrayList();
             patternList.add(Pair.of(BannerPattern.BASE, baseColor));
            if (bannerNBT != null) {
                // Get the patterns from the NBT data
                ListNBT patterns = bannerNBT.getList("Patterns", 10);
                int size = patterns.size();
                for (int i = 0; i < size; ++i) {
                    CompoundNBT pattern = patterns.getCompound(i);
                    String patternHash = pattern.getString("Pattern");
                    Optional<BannerPattern> bannerPattern = Arrays.stream(BannerPattern.values()).filter(p -> p.getHashname().equals(patternHash)).findFirst();
                    if (bannerPattern .isPresent()) {
                        DyeColor bannerPatternColor = DyeColor.byId(pattern.getInt("Color"));
                        patternList.add(Pair.of(bannerPattern.get(), bannerPatternColor));
                    }
                }
            }

            return patternList;
        }
        return null;
    }
}
