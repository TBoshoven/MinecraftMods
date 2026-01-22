package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
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
        for (RegistryObject<MagicDoorknobItem> i : Items.DOORKNOBS.values()) {
            tag(MAGIC_DOORKNOB).add(i.getKey().cast(Registries.ITEM).orElseThrow());
        }
    }
}
