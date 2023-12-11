package com.tomboshoven.minecraft.magicmirror.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/**
 * Collection of all commands in the mod.
 */
public final class Commands {
    private static final MagicMirrorCommand MAGIC_MIRROR = new MagicMirrorCommand();

    private Commands() {
    }

    /**
     * Register all commands.
     *
     * @param dispatcher The command dispatcher to register our commands to.
     */
    private static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        MAGIC_MIRROR.register(dispatcher);
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Commands::serverStarting);
    }

    private static void serverStarting(FMLServerStartingEvent event) {
        registerCommands(event.getCommandDispatcher());
    }
}
