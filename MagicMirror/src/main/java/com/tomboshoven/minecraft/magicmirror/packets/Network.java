package com.tomboshoven.minecraft.magicmirror.packets;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror.MessageAttachModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor.MessageSwapMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor.MessageSwapPlayer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Class managing custom networking, such as sending and registering of messages.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Network {
    private static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(ModMagicMirror.MOD_ID);

    private Network() {
    }

    /**
     * Send a message to all clients tracking a specific position.
     *
     * @param message The message to send.
     * @param world   The world containing the tracked block.
     * @param pos     The position of the tracked block in the world.
     */
    public static void sendToAllTracking(IMessage message, World world, BlockPos pos) {
        CHANNEL.sendToAllTracking(message, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
    }

    /**
     * Send a message to all clients tracking a specific player.
     *
     * @param message The message to send.
     * @param player  The tracked player.
     */
    public static void sendToAllTracking(IMessage message, EntityPlayer player) {
        CHANNEL.sendToAllTracking(message, player);
    }

    /**
     * Send a message to a specific player.
     *
     * @param message The message to send.
     * @param player  The player to send the message to.
     */
    public static void sendTo(IMessage message, EntityPlayerMP player) {
        CHANNEL.sendTo(message, player);
    }

    /**
     * Register all messages.
     */
    @SuppressWarnings("UnusedAssignment")
    public static void registerMessages() {
        int id = 0;
        CHANNEL.registerMessage(MagicMirrorTileEntityModifierArmor.messageHandlerSwapMirror, MessageSwapMirror.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(MagicMirrorTileEntityModifierArmor.messageHandlerSwapPlayer, MessageSwapPlayer.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(BlockMagicMirror.messageHandlerAttachModifier, MessageAttachModifier.class, id++, Side.CLIENT);
    }
}
