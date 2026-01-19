package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public final class ItemTags extends ItemTagsProvider {
    /**
     * Tag containing all doorknobs.
     */
    public static Tag<Item> MAGIC_DOORKNOB = new Tag<>(new ResourceLocation(MagicDoorknobMod.MOD_ID, "magic_doorknob"));

    public ItemTags(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void addTags() {
        // Add all doorknobs to the doorknobs tag
        for (MagicDoorknobItem d : Items.DOORKNOBS.values()) {
            tag(MAGIC_DOORKNOB).add(d);
        }
    }
}
