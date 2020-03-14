package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorCore;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorPart;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Manager of all renderers in the mod.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Renderers {
    private Renderers() {
    }

    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event) {
        TileEntityMagicMirrorRenderer renderer = new TileEntityMagicMirrorRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagicMirrorCore.class, renderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagicMirrorPart.class, renderer);
    }
}
