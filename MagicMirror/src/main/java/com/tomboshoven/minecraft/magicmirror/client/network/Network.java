package com.tomboshoven.minecraft.magicmirror.client.network;

import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorCoreBlock;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

/**
 * Class managing client-side registration of messages.
 */
public final class Network {
    private Network() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Network::registerClientPayloadHandlers);
    }

    /**
     * Register all messages.
     */
    private static void registerClientPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(ArmorMagicMirrorBlockEntityModifier.MessageEquip.TYPE, ArmorMagicMirrorBlockEntityModifier::onMessageEquip);
        event.register(ArmorMagicMirrorBlockEntityModifier.MessageSwapMirror.TYPE, ArmorMagicMirrorBlockEntityModifier::onMessageSwapMirror);
        event.register(ArmorMagicMirrorBlockEntityModifier.MessageSwapPlayer.TYPE, ArmorMagicMirrorBlockEntityModifier::onMessageSwapPlayer);
        event.register(MagicMirrorCoreBlock.MessageAttachModifier.TYPE, MagicMirrorCoreBlock::onMessageAttachModifier);
    }
}
