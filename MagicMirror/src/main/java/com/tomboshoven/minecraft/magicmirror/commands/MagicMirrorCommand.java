package com.tomboshoven.minecraft.magicmirror.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.ReflectionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * Commands related to the mod.
 * Currently, this only features a debug command.
 */
final class MagicMirrorCommand {
    private MagicMirrorCommand() {
    }

    /**
     * Reply with some debugging information.
     *
     * @param context The command context.
     * @return Currently, the number of active reflections.
     */
    private static int debug(CommandContext<? extends CommandSourceStack> context) {
        int reflectionCount = ReflectionManager.countReflections();
        int activeCount = Reflection.countActiveReflections();
        context.getSource().sendSuccess(() -> Component.translatable("commands.magic_mirror.debug.reflections", reflectionCount, activeCount), true);
        return activeCount;
    }

    /**
     * Register the command.
     *
     * @param dispatcher The command dispatcher to register the command to.
     */
    static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                net.minecraft.commands.Commands.literal("magic_mirror").then(
                        net.minecraft.commands.Commands.literal("debug").executes(
                                MagicMirrorCommand::debug
                        )
                )
        );
    }
}
