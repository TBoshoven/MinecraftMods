package com.tomboshoven.minecraft.magicmirror;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifierArmor;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifierCreature;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntities;
import com.tomboshoven.minecraft.magicmirror.commands.Commands;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import com.tomboshoven.minecraft.magicmirror.renderers.Renderers;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(ModMagicMirror.MOD_ID)
public class ModMagicMirror {
    public static final String MOD_ID = "magic_mirror";

    @SuppressWarnings("PublicField")
    public static final Logger LOGGER = LogManager.getLogger();

    public ModMagicMirror() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(Blocks.class);
        modEventBus.register(Items.class);
        modEventBus.register(TileEntities.class);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> modEventBus.register(Renderers.class));

        // Register packets
        Network.registerMessages();

        MinecraftForge.EVENT_BUS.register(Commands.class);

        // Register modifiers
        MagicMirrorModifier.register(new MagicMirrorModifierArmor());
        MagicMirrorModifier.register(new MagicMirrorModifierCreature());
    }
}
