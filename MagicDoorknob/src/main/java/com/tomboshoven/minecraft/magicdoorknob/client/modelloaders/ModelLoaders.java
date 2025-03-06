package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders;

import com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured.TexturedUnbakedModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

import static com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod.MOD_ID;

/**
 * Collection of custom model loaders.
 */
public final class ModelLoaders {
    public static final ResourceLocation TEXTURED_MODEL_LOADER_KEY = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textured");

    private ModelLoaders() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModelLoaders::registerModelLoaders);
    }

    private static void registerModelLoaders(ModelEvent.RegisterLoaders event) {
        event.register(TEXTURED_MODEL_LOADER_KEY, new TexturedUnbakedModelLoader());
    }
}
