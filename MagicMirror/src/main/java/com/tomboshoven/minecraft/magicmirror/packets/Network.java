package com.tomboshoven.minecraft.magicmirror.packets;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorCoreBlock;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorCoreBlock.MessageAttachModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageEquip;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageSwapMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageSwapPlayer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Class managing custom networking, such as sending and registering of messages.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Network {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MagicMirrorMod.MOD_ID, "channel"),
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
        CHANNEL.registerMessage(id++, MessageEquip.class, MessageEquip::encode, MessageEquip::decode, ArmorMagicMirrorBlockEntityModifier::onMessageEquip);
        CHANNEL.registerMessage(id++, MessageSwapMirror.class, MessageSwapMirror::encode, MessageSwapMirror::decode, ArmorMagicMirrorBlockEntityModifier::onMessageSwapMirror);
        CHANNEL.registerMessage(id++, MessageSwapPlayer.class, MessageSwapPlayer::encode, MessageSwapPlayer::decode, ArmorMagicMirrorBlockEntityModifier::onMessageSwapPlayer);
        CHANNEL.registerMessage(id++, MessageAttachModifier.class, MessageAttachModifier::encode, MessageAttachModifier::decode, MagicMirrorCoreBlock::onMessageAttachModifier);
    }
}
