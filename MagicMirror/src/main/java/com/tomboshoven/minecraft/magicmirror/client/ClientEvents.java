package com.tomboshoven.minecraft.magicmirror.client;

import com.tomboshoven.minecraft.magicmirror.client.reflection.ReflectionManager;
import com.tomboshoven.minecraft.magicmirror.client.renderers.Renderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientEvents {
    public static void init(IEventBus modEventBus) {
        Renderers.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(ReflectionManager.class);
    }
}
