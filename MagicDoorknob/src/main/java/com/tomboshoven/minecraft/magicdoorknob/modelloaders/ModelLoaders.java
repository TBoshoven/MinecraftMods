package com.tomboshoven.minecraft.magicdoorknob.modelloaders;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Collection of custom model loaders.
 */
public final class ModelLoaders {
    @SidedProxy(
            serverSide = "com.tomboshoven.minecraft.magicdoorknob.modelloaders.ModelLoaders$ModelLoaderRegistration",
            clientSide = "com.tomboshoven.minecraft.magicdoorknob.modelloaders.ModelLoaders$ModelLoaderRegistrationClient"
    )
    private static ModelLoaderRegistration modelLoaderRegistration;

    private ModelLoaders() {
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelRegistryEvent event) {
        //noinspection StaticVariableUsedBeforeInitialization
        modelLoaderRegistration.registerModelLoaders();
    }

    /**
     * Class for model loader registration.
     * This class is intended for servers. See ModelLoaderRegistrationClient for the client version.
     */
    @SuppressWarnings("WeakerAccess")
    public static class ModelLoaderRegistration {
        /**
         * Register all model loaders.
         */
        void registerModelLoaders() {
            // Do nothing on server side
        }
    }

    /**
     * Class for model loader registration.
     * This class is intended for clients. See ModelLoaderRegistration for the server version.
     */
    @SuppressWarnings("unused")
    @SideOnly(Side.CLIENT)
    public static class ModelLoaderRegistrationClient extends ModelLoaderRegistration {
        @Override
        void registerModelLoaders() {
            super.registerModelLoaders();

            // Initialize textured model loader
            TexturedModelLoader modelLoader = new TexturedModelLoader();
            modelLoader.register(
                    new ResourceLocation("magic_doorknob", "magic_doorway"),
                    new ResourceLocation("magic_doorknob", "textured/magic_doorway")
            );
            modelLoader.register(
                    new ResourceLocation("magic_doorknob", "magic_door"),
                    new ResourceLocation("magic_doorknob", "textured/magic_door")
            );
            modelLoader.registerTexture(new ResourceLocation("magic_doorknob", "blocks/empty"));
            ModelLoaderRegistry.registerLoader(modelLoader);
        }
    }
}
