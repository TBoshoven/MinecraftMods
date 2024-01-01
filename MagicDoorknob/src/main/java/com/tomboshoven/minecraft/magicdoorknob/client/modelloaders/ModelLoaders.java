package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders;

import com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured.TexturedGeometryLoader;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Collection of custom model loaders.
 */
public final class ModelLoaders {
    private ModelLoaders() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModelLoaders::registerModelLoaders);
    }

    private static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        // Initialize textured model loader
        TexturedGeometryLoader modelLoader = new TexturedGeometryLoader();
        event.register("textured", modelLoader);
    }
}
