package com.tomboshoven.minecraft.magicmirror.commands;

import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Collection of all commands in the mod.
 */
public final class Commands {
    private static final ICommand commandMagicMirror = new MagicMirrorCommand();

    private Commands() {
    }

    /**
     * Register all commands.
     */
    public static void registerCommands() {
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientCommandHandler.instance.registerCommand(commandMagicMirror);
        }
    }
}
