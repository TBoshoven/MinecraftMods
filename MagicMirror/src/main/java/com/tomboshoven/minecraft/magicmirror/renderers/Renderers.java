package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Manager of all renderers in the mod.
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Renderers {
    private Renderers() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Renderers::registerRenderers);
    }

    private static void registerRenderers(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntityRenderer(TileEntities.MAGIC_MIRROR_CORE.get(), TileEntityMagicMirrorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntities.MAGIC_MIRROR_PART.get(), TileEntityMagicMirrorRenderer::new);
    }
}
