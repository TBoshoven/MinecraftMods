package com.tomboshoven.minecraft.magicmirror;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.BlockEntities;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifiers;
import com.tomboshoven.minecraft.magicmirror.client.ClientEvents;
import com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers.ReflectionModifiers;
import com.tomboshoven.minecraft.magicmirror.commands.Commands;
import com.tomboshoven.minecraft.magicmirror.data.DataGenerators;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import com.tomboshoven.minecraft.magicmirror.network.Network;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"UtilityClassWithoutPrivateConstructor", "UtilityClassWithPublicConstructor"})
@Mod(MagicMirrorMod.MOD_ID)
public final class MagicMirrorMod {
    public static final String MOD_ID = "magic_mirror";

    public static final Logger LOGGER = LogManager.getLogger();

    public MagicMirrorMod(IEventBus modEventBus) {
        Blocks.register(modEventBus);
        DataGenerators.register(modEventBus);
        Items.register(modEventBus);
        BlockEntities.register(modEventBus);
        MagicMirrorModifiers.register(modEventBus);

        // Register packets
        Network.register(modEventBus);

        Commands.register(NeoForge.EVENT_BUS);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ClientEvents.init(modEventBus);
            ReflectionModifiers.register(modEventBus);
        }
    }
}
