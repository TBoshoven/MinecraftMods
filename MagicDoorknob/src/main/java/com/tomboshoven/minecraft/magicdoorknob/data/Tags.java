package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

final class Tags extends ItemTagsProvider {
    public Tags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MagicDoorknobMod.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagKey<Item> ENCHANTABLE_MINING = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("enchantable/mining"));
        this.tag(ENCHANTABLE_MINING).add(Items.DOORKNOBS.values().stream().map(i -> i.get()).toArray(Item[]::new));
    }
}
