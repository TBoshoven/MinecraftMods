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
@OnlyIn(Dist.CLIENT)
public final class ModelLoaders {
    private ModelLoaders() {
    }

    private static String[] TEXTURED_MODELS = new String[] {
            "magic_door", "magic_door_knob", "magic_doorknob", "magic_doorway_pillar", "magic_doorway_pillar_short",
            "magic_doorway_top", "magic_doorway_wall", "magic_doorway_wall_narrow", "magic_doorway_wall_narrow_short",
            "magic_doorway_wall_short",
    };

    @SubscribeEvent
    public static void registerModelLoaders(ModelRegistryEvent event) {
        // Initialize textured model loader
        TexturedModelLoader modelLoader = new TexturedModelLoader();
        // Register all textured models with the model loader
        for (String name : TEXTURED_MODELS) {
            modelLoader.register(
                    new ResourceLocation(MOD_ID, "block/" + name),
                    new ResourceLocation(MOD_ID, "block/textured/" + name)
            );
        }
        modelLoader.registerTexture(new ResourceLocation("magic_doorknob", "block/empty"));
        ModelLoaderRegistry.registerLoader(modelLoader);
    }
}
