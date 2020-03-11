package com.tomboshoven.minecraft.magicmirror.commands;

import com.mojang.brigadier.CommandDispatcher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all commands in the mod.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Commands {
    static MagicMirrorCommand MAGIC_MIRROR = new MagicMirrorCommand();

    private Commands() {
    }

    /**
     * Register all commands.
     *
     * @param dispatcher The command dispatcher to register our commands to.
     */
    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        MAGIC_MIRROR.register(dispatcher);
    }

    @SubscribeEvent
    public static void serverStarting(FMLServerStartingEvent event) {
        registerCommands(event.getCommandDispatcher());
    }
}
