package com.tomboshoven.minecraft.magicmirror.packets;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror.MessageAttachModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor.MessageSwapMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor.MessageSwapPlayer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Class managing custom networking, such as sending and registering of messages.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Network {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ModMagicMirror.MOD_ID, "channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private Network() {
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
