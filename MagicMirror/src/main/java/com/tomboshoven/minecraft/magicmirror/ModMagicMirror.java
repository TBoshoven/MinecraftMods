package com.tomboshoven.minecraft.magicmirror;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifierArmor;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifierCreature;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorCore;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorPart;
import com.tomboshoven.minecraft.magicmirror.commands.Commands;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import com.tomboshoven.minecraft.magicmirror.packets.Network;
import com.tomboshoven.minecraft.magicmirror.renderers.Renderers;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(ModMagicMirror.MOD_ID)
public class ModMagicMirror {
    public static final String MOD_ID = "magic_mirror";
    static final String NAME = "Magic Mirror";

    @SuppressWarnings("PublicField")
    public static Logger logger;

    @SuppressWarnings("MethodMayBeStatic")
    @EventHandler
    public void init(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(Blocks.class);
        MinecraftForge.EVENT_BUS.register(Items.class);
        MinecraftForge.EVENT_BUS.register(Renderers.class);

        // Register packets
        Network.registerMessages();

        // Register tile entities
        GameRegistry.registerTileEntity(TileEntityMagicMirrorCore.class, new ResourceLocation(MOD_ID, "magic_mirror_core"));
        GameRegistry.registerTileEntity(TileEntityMagicMirrorPart.class, new ResourceLocation(MOD_ID, "magic_mirror"));

        // Register commands
        Commands.registerCommands();

        // Register modifiers
        MagicMirrorModifier.register(new MagicMirrorModifierArmor());
        MagicMirrorModifier.register(new MagicMirrorModifierCreature());
    }
}
