package com.tomboshoven.minecraft.magicdoorknob;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntities;
import com.tomboshoven.minecraft.magicdoorknob.config.Config;
import com.tomboshoven.minecraft.magicdoorknob.data.DataGenerators;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.ModelLoaders;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
        TileEntities.register(modEventBus);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            BlockColorHandlers.register(modEventBus);
            ModelLoaders.register(modEventBus);
        });
    }
}
