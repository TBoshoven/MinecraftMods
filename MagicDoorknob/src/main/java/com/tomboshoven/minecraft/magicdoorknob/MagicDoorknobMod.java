package com.tomboshoven.minecraft.magicdoorknob;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntities;
import com.tomboshoven.minecraft.magicdoorknob.data.DataGenerators;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.ModelLoaders;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(MagicDoorknobMod.MOD_ID)
public class MagicDoorknobMod {
    public static final String MOD_ID = "magic_doorknob";

    public MagicDoorknobMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(BlockColorHandlers.class);
        modEventBus.register(Blocks.class);
        modEventBus.register(DataGenerators.class);
        modEventBus.register(Items.class);
        modEventBus.register(TileEntities.class);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> modEventBus.register(ModelLoaders.class));
    }
}
