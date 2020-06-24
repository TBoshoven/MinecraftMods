package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorPartTileEntity;
import mcp.MethodsReturnNonnullByDefault;
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
        TileEntityMagicMirrorRenderer renderer = new TileEntityMagicMirrorRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(MagicMirrorCoreTileEntity.class, renderer);
        ClientRegistry.bindTileEntitySpecialRenderer(MagicMirrorPartTileEntity.class, renderer);
    }
}
