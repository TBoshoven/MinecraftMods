package com.tomboshoven.minecraft.magicmirror;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.BlockEntities;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.ArmorMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.BannerMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.CreatureMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.DyeMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.client.ClientEvents;
import com.tomboshoven.minecraft.magicmirror.commands.Commands;
import com.tomboshoven.minecraft.magicmirror.data.DataGenerators;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MagicMirrorMod.MOD_ID)
public final class MagicMirrorMod {
    public static final String MOD_ID = "magic_mirror";

    public static final Logger LOGGER = LogManager.getLogger();

    public MagicMirrorMod(IEventBus modEventBus) {
        Blocks.register(modEventBus);
        DataGenerators.register(modEventBus);
        Items.register(modEventBus);
        BlockEntities.register(modEventBus);

        // Register packets
        Network.registerMessages();

        Commands.register(NeoForge.EVENT_BUS);

        // Register modifiers
        MagicMirrorModifier.register(new ArmorMagicMirrorModifier());
        MagicMirrorModifier.register(new BannerMagicMirrorModifier());
        MagicMirrorModifier.register(new CreatureMagicMirrorModifier());
        MagicMirrorModifier.register(new DyeMagicMirrorModifier());

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEvents.init(modEventBus);
        }
    }
}
