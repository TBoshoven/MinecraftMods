package com.tomboshoven.minecraft.magicdoorknob.modelloaders;

import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedGeometryLoader;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import static com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod.MOD_ID;

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
        modelLoader.registerTexture(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MOD_ID, "block/empty")));
        event.register("textured", modelLoader);
    }
}
