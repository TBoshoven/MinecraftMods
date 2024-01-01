package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders;

import com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured.TexturedModelLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry2;
import net.minecraftforge.eventbus.api.IEventBus;

import static com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod.MOD_ID;

/**
 * Collection of custom model loaders.
 */
public final class ModelLoaders {
    private ModelLoaders() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModelLoaders::registerModelLoaders);
    }

   private static void registerModelLoaders(ModelRegistryEvent event) {
        // Initialize textured model loader
        TexturedModelLoader modelLoader = new TexturedModelLoader();
        modelLoader.registerTexture(new ResourceLocation(MOD_ID, "block/empty"));
        ModelLoaderRegistry2.registerLoader(new ResourceLocation(MOD_ID, "textured"), modelLoader);
    }
}
