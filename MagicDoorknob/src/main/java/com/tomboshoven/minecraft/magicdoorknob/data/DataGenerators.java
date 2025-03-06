package com.tomboshoven.minecraft.magicdoorknob.data;

import net.minecraft.core.HolderLookup;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(DataGenerators::gatherData);
    }

    // NeoForge recommend generating all data as part of the client event
    private static void gatherData(GatherDataEvent.Client event) {
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        event.createProvider(Models::new);
        event.createProvider(Language::new);
        event.createProvider(output -> new Recipes.Runner(output, lookupProvider));
    }
}
