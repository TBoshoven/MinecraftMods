package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

final class Tags {
    final static class BlockTags extends BlockTagsProvider {
        public BlockTags(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
            super(dataGenerator, MagicDoorknobMod.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags() {}
    }

    final static class ItemTags extends ItemTagsProvider {
        public ItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTags, @Nullable ExistingFileHelper existingFileHelper) {
            super(dataGenerator, blockTags, MagicDoorknobMod.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags() {
        }
    }
}
