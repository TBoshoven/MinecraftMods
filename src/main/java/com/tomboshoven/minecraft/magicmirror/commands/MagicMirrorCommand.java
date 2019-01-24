package com.tomboshoven.minecraft.magicmirror.commands;

import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorCommand extends CommandBase {

    @Override
    public String getName() {
        return "magic_mirror";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.magic_mirror.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1 || !"debug".equals(args[0])) {
            throw new WrongUsageException("commands.magic_mirror.usage");
        }
        sender.sendMessage(new TextComponentTranslation("commands.magic_mirror.debug.reflections", Reflection.getActiveReflections()));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(new String[]{"debug"});
        }
        return Collections.<String>emptyList();
    }
}
