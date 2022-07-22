package com.tomboshoven.minecraft.magicdoorknob.data;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
        generator.addProvider(includeServer, new BlockStates(generator, existingFileHelper));
        generator.addProvider(includeServer, new Language(generator));
        generator.addProvider(includeServer, new Recipes(generator));
        generator.addProvider(includeClient, new ItemModels(generator, existingFileHelper));
    }
}
