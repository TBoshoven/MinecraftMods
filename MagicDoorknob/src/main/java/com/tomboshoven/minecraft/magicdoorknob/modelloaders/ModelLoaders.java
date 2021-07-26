package com.tomboshoven.minecraft.magicdoorknob.modelloaders;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured.TexturedModelLoader;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
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

   private static void registerModelLoaders(ModelRegistryEvent event) {
        // Initialize textured model loader
        TexturedModelLoader modelLoader = new TexturedModelLoader();
        modelLoader.registerTexture(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MOD_ID, "block/empty")));
        ModelLoaderRegistry.registerLoader(new ResourceLocation(MOD_ID, "textured"), modelLoader);

        // Translucent has the best results across block types
        ItemBlockRenderTypes.setRenderLayer(Blocks.MAGIC_DOOR.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(Blocks.MAGIC_DOORWAY.get(), RenderType.translucent());
    }
}
