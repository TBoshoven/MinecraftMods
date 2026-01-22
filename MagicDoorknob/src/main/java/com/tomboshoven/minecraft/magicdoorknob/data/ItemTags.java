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
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;

final class ItemTags extends ItemTagsProvider {
    /**
     * Tag containing all doorknobs.
     */
    static TagKey<Item> MAGIC_DOORKNOB = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MagicDoorknobMod.MOD_ID, "magic_doorknob"));

    public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MagicDoorknobMod.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Add all doorknobs to the doorknobs tag
        tag(MAGIC_DOORKNOB).addAll(Items.DOORKNOBS.values().stream().map(DeferredHolder::get));
        // Add the doorknobs tag to the tag that allows them to be enchanted with "mining efficiency"
        TagKey<Item> enchantableMining = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("enchantable/mining"));
        tag(enchantableMining).addTag(MAGIC_DOORKNOB);
    }
}
