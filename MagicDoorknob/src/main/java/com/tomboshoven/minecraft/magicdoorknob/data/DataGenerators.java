package com.tomboshoven.minecraft.magicdoorknob.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(DataGenerators::gatherData);
    }

    // NeoForge recommend generating all data as part of the client event
    private static void gatherData(GatherDataEvent.Client event) {
        event.createWorldRegistryObjects(new RegistrySetBuilder().add(Registries.ENCHANTMENT, Enchantments::bootstrap));
        event.createReloadableRegistryObjects(new RegistrySetBuilder().add(Recipes.asBootstrap(Recipes::new)));
        event.createProvider(EnchantmentTags::new);
        event.createProvider(ItemTags::new);
        event.createProvider(Models::new);
        event.createProvider(Language::new);
    }
}
