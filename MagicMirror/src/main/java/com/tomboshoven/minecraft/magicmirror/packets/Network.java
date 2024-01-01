package com.tomboshoven.minecraft.magicmirror.packets;

import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorCoreBlock;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorCoreBlock.MessageAttachModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageEquip;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageSwapMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.MessageSwapPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

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
    public static void registerPayloadHandlers(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(MOD_ID);

        registrar.play(MessageEquip.ID, MessageEquip::new, handler -> handler.client(ArmorMagicMirrorBlockEntityModifier::onMessageEquip));
        registrar.play(MessageSwapMirror.ID, MessageSwapMirror::new, handler -> handler.client(ArmorMagicMirrorBlockEntityModifier::onMessageSwapMirror));
        registrar.play(MessageSwapPlayer.ID, MessageSwapPlayer::new, handler -> handler.client(ArmorMagicMirrorBlockEntityModifier::onMessageSwapPlayer));
        registrar.play(MessageAttachModifier.ID, MessageAttachModifier::new, handler -> handler.client(MagicMirrorCoreBlock::onMessageAttachModifier));
    }
}
