package com.tomboshoven.minecraft.magicmirror.client;

import com.tomboshoven.minecraft.magicmirror.client.network.Network;
import com.tomboshoven.minecraft.magicmirror.client.reflection.ReflectionManager;
import com.tomboshoven.minecraft.magicmirror.client.renderers.Renderers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientEvents {
    private ClientEvents() {
    }

    public static void init(IEventBus modEventBus) {
        Network.register(modEventBus);
        Renderers.register(modEventBus);
        NeoForge.EVENT_BUS.register(ReflectionManager.class);
    }
}
