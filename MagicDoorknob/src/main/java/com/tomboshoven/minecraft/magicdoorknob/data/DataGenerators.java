package com.tomboshoven.minecraft.magicdoorknob.data;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(DataGenerators::gatherData);
        eventBus.addListener(DataGenerators::gatherDataRegistryEntries);
    }

    // NeoForge recommend generating all data as part of the client event
    private static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(EnchantmentTags::new);
        event.createProvider(ItemTags::new);
        event.createProvider(Models::new);
        event.createProvider(Language::new);
    }

    private static void gatherDataRegistryEntries(GatherDataRegistryEntriesEvent event) {
        event.add(Registries.ENCHANTMENT, Enchantments::bootstrap);
        event.recipe(Recipes::new);
    }
}
