package com.tomboshoven.minecraft.magicdoorknob.modelloaders;

import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedModelLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry2;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod.MOD_ID;

/**
 * Collection of custom model loaders.
 */
@OnlyIn(Dist.CLIENT)
public final class ModelLoaders {
    private ModelLoaders() {
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelRegistryEvent event) {
        // Initialize textured model loader
        TexturedModelLoader modelLoader = new TexturedModelLoader();
        modelLoader.registerTexture(new ResourceLocation(MOD_ID, "block/empty"));
        ModelLoaderRegistry2.registerLoader(new ResourceLocation(MOD_ID, "textured"), modelLoader);
    }
}
