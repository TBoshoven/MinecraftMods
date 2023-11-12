package com.tomboshoven.minecraft.magicmirror.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.IEventBus;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all commands in the mod.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Commands {
    private static final MagicMirrorCommand MAGIC_MIRROR = new MagicMirrorCommand();

    private Commands() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Commands::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        MAGIC_MIRROR.register(dispatcher);
    }
}
