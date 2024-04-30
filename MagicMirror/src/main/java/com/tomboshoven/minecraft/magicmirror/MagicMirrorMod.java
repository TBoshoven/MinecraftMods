package com.tomboshoven.minecraft.magicmirror;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifiers;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntities;
import com.tomboshoven.minecraft.magicmirror.client.ClientEvents;
import com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers.ReflectionModifiers;
import com.tomboshoven.minecraft.magicmirror.commands.Commands;
import com.tomboshoven.minecraft.magicmirror.data.DataGenerators;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MagicMirrorMod.MOD_ID)
public final class MagicMirrorMod {
    public static final String MOD_ID = "magic_mirror";

    public static final Logger LOGGER = LogManager.getLogger();

    public MagicMirrorMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Blocks.register(modEventBus);
        DataGenerators.register(modEventBus);
        Items.register(modEventBus);
        TileEntities.register(modEventBus);
        MagicMirrorModifiers.register(modEventBus);

        // Register packets
        Network.registerMessages();

        Commands.register(MinecraftForge.EVENT_BUS);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEvents.init(modEventBus);
            ReflectionModifiers.register(modEventBus);
        }
    }
}
