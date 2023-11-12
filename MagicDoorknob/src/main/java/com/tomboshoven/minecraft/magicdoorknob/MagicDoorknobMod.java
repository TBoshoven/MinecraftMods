package com.tomboshoven.minecraft.magicdoorknob;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.BlockEntities;
import com.tomboshoven.minecraft.magicdoorknob.config.Config;
import com.tomboshoven.minecraft.magicdoorknob.data.DataGenerators;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.ModelLoaders;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(MagicDoorknobMod.MOD_ID)
public final class MagicDoorknobMod {
    public static final String MOD_ID = "magic_doorknob";

    public MagicDoorknobMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.register(ModLoadingContext.get());
        Blocks.register(modEventBus);
        DataGenerators.register(modEventBus);
        Items.register(modEventBus);
        BlockEntities.register(modEventBus);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.register(BlockColorHandlers.class);
            modEventBus.register(ModelLoaders.class);
        });
    }
}
