package com.tomboshoven.minecraft.magicmirror.packets;

import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorCoreBlock;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorCoreBlock.MessageAttachModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageEquip;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageSwapMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageSwapPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.tomboshoven.minecraft.magicmirror.MagicMirrorMod.MOD_ID;

/**
 * Class managing custom networking, such as sending and registering of messages.
 */
public final class Network {
    private Network() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Network::registerPayloadHandlers);
    }

    /**
     * Register all messages.
     */
    public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MOD_ID);

        registrar.playToClient(MessageEquip.TYPE, MessageEquip.STREAM_CODEC, ArmorMagicMirrorBlockEntityModifier::onMessageEquip);
        registrar.playToClient(MessageSwapMirror.TYPE, MessageSwapMirror.STREAM_CODEC, ArmorMagicMirrorBlockEntityModifier::onMessageSwapMirror);
        registrar.playToClient(MessageSwapPlayer.TYPE, MessageSwapPlayer.STREAM_CODEC, ArmorMagicMirrorBlockEntityModifier::onMessageSwapPlayer);
        registrar.playToClient(MessageAttachModifier.TYPE, MessageAttachModifier.STREAM_CODEC, MagicMirrorCoreBlock::onMessageAttachModifier);
    }
}
