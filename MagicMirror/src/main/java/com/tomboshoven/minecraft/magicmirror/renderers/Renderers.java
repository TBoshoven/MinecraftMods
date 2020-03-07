package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorCore;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorPart;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Manager of all renderers in the mod.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Renderers {
    /**
     * Sided proxy for renderer registration.
     * On the server side, this does nothing.
     */
    @SidedProxy(
            serverSide = "com.tomboshoven.minecraft.magicmirror.renderers.Renderers$RenderRegistration",
            clientSide = "com.tomboshoven.minecraft.magicmirror.renderers.Renderers$RenderRegistrationClient"
    )
    private static RenderRegistration renderRegistration;

    private Renderers() {
    }

    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event) {
        //noinspection StaticVariableUsedBeforeInitialization
        renderRegistration.registerRenderers();
    }

    /**
     * Class for renderer registration.
     * This class is intended for servers. See RenderRegistrationClient for the client version.
     */
    public static class RenderRegistration {
        /**
         * Register all renderers.
         */
        void registerRenderers() {
            // Do nothing on server side
        }
    }

    /**
     * Class for renderer registration.
     * This class is intended for clients. See RenderRegistration for the server version.
     */
    @OnlyIn(Dist.CLIENT)
    public static class RenderRegistrationClient extends RenderRegistration {
        @Override
        void registerRenderers() {
            super.registerRenderers();

            TileEntityMagicMirrorRenderer renderer = new TileEntityMagicMirrorRenderer();
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagicMirrorCore.class, renderer);
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagicMirrorPart.class, renderer);

            ModelLoader.setCustomModelResourceLocation(
                    Items.itemBlockMagicMirror,
                    0,
                    new ModelResourceLocation(
                            new ResourceLocation(ModMagicMirror.MOD_ID, "magic_mirror"),
                            "inventory"
                    )
            );
        }
    }
}
