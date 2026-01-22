package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.enchantments.Enchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

final class EnchantmentTags extends EnchantmentTagsProvider {
    public EnchantmentTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FeatureFlagSet.of());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagKey<Enchantment> nonTreasure = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation("non_treasure"));
        tag(nonTreasure).add(Enchantments.DOUBLE.get());
    }
}
