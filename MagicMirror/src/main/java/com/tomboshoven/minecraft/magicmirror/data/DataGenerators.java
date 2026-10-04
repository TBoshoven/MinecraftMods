package com.tomboshoven.minecraft.magicmirror.data;

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
        event.createReloadableRegistryObjects(
                new RegistrySetBuilder()
                        .add(Recipes.asBootstrap(Recipes::new))
                        .add(Registries.LOOT_TABLE, new LootTables())
        );
        event.createProvider(Language::new);
        event.createProvider(Models::new);
    }
}
