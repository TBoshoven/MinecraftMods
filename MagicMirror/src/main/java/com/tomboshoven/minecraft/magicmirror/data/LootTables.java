package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

class LootTables extends LootTableProvider {
    LootTables(PackOutput output) {
        super(output, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)));
    }

    private static class BlockLoot extends BlockLootSubProvider {
        protected BlockLoot() {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            Item item = Items.MAGIC_MIRROR.get();
            dropOther(Blocks.MAGIC_MIRROR_CORE.get(), item);
            dropOther(Blocks.MAGIC_MIRROR_PART.get(), item);
            dropOther(Blocks.MAGIC_MIRROR_INACTIVE.get(), item);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Stream.of(
                    Blocks.MAGIC_MIRROR_CORE, Blocks.MAGIC_MIRROR_PART, Blocks.MAGIC_MIRROR_INACTIVE
            ).map(Supplier::get)::iterator;
        }
    }
}
