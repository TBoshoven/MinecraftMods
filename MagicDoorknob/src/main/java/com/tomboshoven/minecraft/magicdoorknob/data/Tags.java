package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

final class Tags {
    final static class BlockTags extends BlockTagsProvider {
        public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, MagicDoorknobMod.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {}
    }

    final static class ItemTags extends ItemTagsProvider {
        public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, blockTags, MagicDoorknobMod.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            var ENCHANTABLE_MINING = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("enchantable/mining"));
            this.tag(ENCHANTABLE_MINING).add(Items.DOORKNOBS.values().stream().map(i -> i.get()).toArray(Item[]::new));
        }
    }
}
