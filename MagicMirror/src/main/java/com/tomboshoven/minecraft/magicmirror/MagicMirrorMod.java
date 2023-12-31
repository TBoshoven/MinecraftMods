package com.tomboshoven.minecraft.magicmirror;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.ArmorMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.BannerMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.CreatureMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntities;
import com.tomboshoven.minecraft.magicmirror.commands.Commands;
import com.tomboshoven.minecraft.magicmirror.data.DataGenerators;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import com.tomboshoven.minecraft.magicmirror.renderers.Renderers;
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

        // Register packets
        Network.registerMessages();

        Commands.register(MinecraftForge.EVENT_BUS);

        // Register modifiers
        MagicMirrorModifier.register(new ArmorMagicMirrorModifier());
        MagicMirrorModifier.register(new BannerMagicMirrorModifier());
        MagicMirrorModifier.register(new CreatureMagicMirrorModifier());

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEvents.init();
        }
    }

    static class ClientEvents {
        static void init() {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            Renderers.register(modEventBus);
        }
    }
}
