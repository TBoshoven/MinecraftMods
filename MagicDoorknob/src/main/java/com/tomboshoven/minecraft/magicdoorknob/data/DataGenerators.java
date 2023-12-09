package com.tomboshoven.minecraft.magicdoorknob.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(DataGenerators::gatherData);
    }

    private static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(new BlockStates(generator, existingFileHelper));
            generator.addProvider(new Language(generator));
            generator.addProvider(new Recipes(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new ItemModels(generator, existingFileHelper));
        }
    }
}
