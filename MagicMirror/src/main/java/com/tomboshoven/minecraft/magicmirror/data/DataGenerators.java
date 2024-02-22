package com.tomboshoven.minecraft.magicmirror.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(DataGenerators::gatherData);
    }

    private static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();
        generator.addProvider(includeServer, (DataProvider.Factory<DataProvider>) output -> new BlockStates(output, existingFileHelper));
        generator.addProvider(includeServer, (DataProvider.Factory<DataProvider>) Language::new);
        generator.addProvider(includeServer, (DataProvider.Factory<DataProvider>) LootTables::new);
        generator.addProvider(includeServer, (DataProvider.Factory<DataProvider>) Recipes::new);
        generator.addProvider(includeClient, (DataProvider.Factory<DataProvider>) output -> new ItemModels(output, existingFileHelper));
    }
}
