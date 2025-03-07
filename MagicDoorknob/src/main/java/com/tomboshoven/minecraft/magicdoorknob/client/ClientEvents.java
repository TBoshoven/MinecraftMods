package com.tomboshoven.minecraft.magicdoorknob.client;

import com.tomboshoven.minecraft.magicdoorknob.client.blockcolorhandlers.BlockColorHandlers;
import com.tomboshoven.minecraft.magicdoorknob.client.clientextensions.ClientExtensions;
import com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.ModelLoaders;
import net.neoforged.bus.api.IEventBus;

public final class ClientEvents {
    private ClientEvents() {
    }

    public static void init(IEventBus modEventBus) {
        BlockColorHandlers.register(modEventBus);
        ClientExtensions.register(modEventBus);
        ModelLoaders.register(modEventBus);
    }
}
