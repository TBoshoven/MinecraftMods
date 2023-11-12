package com.tomboshoven.minecraft.magicdoorknob.modelloaders;

import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedGeometryLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

/**
 * Collection of custom model loaders.
 */
@OnlyIn(Dist.CLIENT)
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
