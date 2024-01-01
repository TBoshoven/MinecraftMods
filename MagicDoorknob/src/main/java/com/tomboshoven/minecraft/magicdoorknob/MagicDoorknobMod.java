package com.tomboshoven.minecraft.magicdoorknob;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntities;
import com.tomboshoven.minecraft.magicdoorknob.client.ClientEvents;
import com.tomboshoven.minecraft.magicdoorknob.config.Config;
import com.tomboshoven.minecraft.magicdoorknob.data.DataGenerators;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(MagicDoorknobMod.MOD_ID)
public final class MagicDoorknobMod {
    public static final String MOD_ID = "magic_doorknob";

    public MagicDoorknobMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.register(ModLoadingContext.get());
        Blocks.register(modEventBus);
        DataGenerators.register(modEventBus);
        Items.register(modEventBus);
        TileEntities.register(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEvents.init(modEventBus);
        }
    }
}
