package com.tomboshoven.minecraft.magicmirror.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Commands related to the mod.
 * Currently, this only features a debug command.
 */
final class MagicMirrorCommand {
    /**
     * Reply with some debugging information.
     *
     * @param context The command context.
     * @return Currently, the number of active reflections.
     */
    private static int debug(CommandContext<? extends CommandSource> context) {
        int reflectionCount = Reflection.getActiveReflectionsClient();
        context.getSource().sendSuccess(new TranslationTextComponent("commands.magic_mirror.debug.reflections", reflectionCount), true);
        return reflectionCount;
    }

    /**
     * Register the command.
     *
     * @param dispatcher The command dispatcher to register the command to.
     */
    void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                net.minecraft.command.Commands.literal("magic_mirror").then(
                        net.minecraft.command.Commands.literal("debug").executes(
                                MagicMirrorCommand::debug
                        )
                )
        );
    }
}
