package com.tomboshoven.minecraft.magicmirror.packets;

import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorCoreBlock;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
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
    private static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID);

        registrar.playToClient(ArmorMagicMirrorBlockEntityModifier.MessageEquip.TYPE, ArmorMagicMirrorBlockEntityModifier.MessageEquip.STREAM_CODEC, ArmorMagicMirrorBlockEntityModifier::onMessageEquip);
        registrar.playToClient(ArmorMagicMirrorBlockEntityModifier.MessageSwapMirror.TYPE, ArmorMagicMirrorBlockEntityModifier.MessageSwapMirror.STREAM_CODEC, ArmorMagicMirrorBlockEntityModifier::onMessageSwapMirror);
        registrar.playToClient(ArmorMagicMirrorBlockEntityModifier.MessageSwapPlayer.TYPE, ArmorMagicMirrorBlockEntityModifier.MessageSwapPlayer.STREAM_CODEC, ArmorMagicMirrorBlockEntityModifier::onMessageSwapPlayer);
        registrar.playToClient(MagicMirrorCoreBlock.MessageAttachModifier.TYPE, MagicMirrorCoreBlock.MessageAttachModifier.STREAM_CODEC, MagicMirrorCoreBlock::onMessageAttachModifier);
    }
}
