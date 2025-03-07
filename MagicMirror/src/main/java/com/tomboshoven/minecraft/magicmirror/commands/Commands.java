package com.tomboshoven.minecraft.magicmirror.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Collection of all commands in the mod.
 */
public final class Commands {
    private Commands() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Commands::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        MagicMirrorCommand.register(dispatcher);
    }
}
