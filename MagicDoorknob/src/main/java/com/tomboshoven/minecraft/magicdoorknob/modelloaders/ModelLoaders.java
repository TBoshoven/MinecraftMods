package com.tomboshoven.minecraft.magicdoorknob.modelloaders;

import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedModelLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.tomboshoven.minecraft.magicdoorknob.ModMagicDoorknob.MOD_ID;

/**
 * Collection of custom model loaders.
 */
public final class ModelLoaders {
    private ModelLoaders() {
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerModelLoaders(ModelRegistryEvent event) {
        // Initialize textured model loader
        TexturedModelLoader modelLoader = new TexturedModelLoader();
        // Register all textured models with the model loader
        modelLoader.register(
                new ResourceLocation(MOD_ID, "magic_doorway"),
                new ResourceLocation(MOD_ID, "textured/magic_doorway")
        );
        modelLoader.register(
                new ResourceLocation(MOD_ID, "magic_door"),
                new ResourceLocation(MOD_ID, "textured/magic_door")
        );
        modelLoader.register(
                new ResourceLocation(MOD_ID, "magic_doorknob"),
                new ResourceLocation(MOD_ID, "textured/magic_doorknob")
        );
        modelLoader.registerTexture(new ResourceLocation("magic_doorknob", "blocks/empty"));
        ModelLoaderRegistry.registerLoader(modelLoader);
    }
}
