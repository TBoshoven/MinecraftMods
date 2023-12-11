package com.tomboshoven.minecraft.magicmirror.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Collection of all commands in the mod.
 */
public final class Commands {
    private static final MagicMirrorCommand MAGIC_MIRROR = new MagicMirrorCommand();

    private Commands() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Commands::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        MAGIC_MIRROR.register(dispatcher);
    }
}
