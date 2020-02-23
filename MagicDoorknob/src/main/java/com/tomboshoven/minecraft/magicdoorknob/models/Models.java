package com.tomboshoven.minecraft.magicdoorknob.models;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.tomboshoven.minecraft.magicdoorknob.ModMagicDoorknob.MOD_ID;

/**
 * Collection of custom models.
 */
public final class Models {
    @SidedProxy(
            serverSide = "com.tomboshoven.minecraft.magicdoorknob.models.Models$ModelRegistration",
            clientSide = "com.tomboshoven.minecraft.magicdoorknob.models.Models$ModelRegistrationClient"
    )
    private static ModelRegistration modelRegistration;

    private Models() {
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        //noinspection StaticVariableUsedBeforeInitialization
        modelRegistration.registerModels();
    }

    /**
     * Class for model registration.
     * This class is intended for servers. See ModelRegistrationClient for the client version.
     */
    @SuppressWarnings("WeakerAccess")
    public static class ModelRegistration {
        /**
         * Register all models.
         */
        void registerModels() {
            // Do nothing on server side
        }
    }

    /**
     * Class for model registration.
     * This class is intended for clients. See ModelRegistration for the server version.
     */
    @SuppressWarnings("unused")
    @SideOnly(Side.CLIENT)
    public static class ModelRegistrationClient extends ModelRegistration {
        @Override
        void registerModels() {
            super.registerModels();

            // Register item models
            ModelResourceLocation modelDoorknob = new ModelResourceLocation(new ResourceLocation(MOD_ID, "magic_doorknob"), null);
            Items.itemDoorknobs.values().forEach(item -> ModelLoader.setCustomModelResourceLocation(item, 0, modelDoorknob));
        }
    }
}
