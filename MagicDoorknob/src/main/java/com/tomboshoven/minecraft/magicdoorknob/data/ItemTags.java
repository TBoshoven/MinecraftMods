package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public final class ItemTags extends ItemTagsProvider {
    /**
     * Tag containing all doorknobs.
     */
    public static Tag.Named<Item> MAGIC_DOORKNOB = ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, new ResourceLocation(MagicDoorknobMod.MOD_ID, "magic_doorknob"));

    public ItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTags, MagicDoorknobMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // Add all doorknobs to the doorknobs tag
        for (MagicDoorknobItem d : Items.DOORKNOBS.values()) {
            tag(MAGIC_DOORKNOB).add(d);
        }
    }
}
