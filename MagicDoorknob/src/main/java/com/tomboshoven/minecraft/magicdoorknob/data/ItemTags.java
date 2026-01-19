package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class ItemTags extends ItemTagsProvider {
    /**
     * Tag containing all doorknobs.
     */
    public static TagKey<Item> MAGIC_DOORKNOB = TagKey.create(Registries.ITEM, new ResourceLocation(MagicDoorknobMod.MOD_ID, "magic_doorknob"));

    public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, MagicDoorknobMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Add all doorknobs to the doorknobs tag
        tag(MAGIC_DOORKNOB).addAll(Items.DOORKNOBS.values().stream().map(DeferredHolder::getKey).toList());
        // Add the doorknobs tag to the tag that allows them to be enchanted with "mining efficiency"
        TagKey<Item> enchantableMining = TagKey.create(Registries.ITEM, new ResourceLocation("enchantable/mining"));
        tag(enchantableMining).addTag(MAGIC_DOORKNOB);
    }
}
