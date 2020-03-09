package com.tomboshoven.minecraft.magicdoorknob;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntities;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.ModelLoaders;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(ModMagicDoorknob.MOD_ID)
public class ModMagicDoorknob {
    public static final String MOD_ID = "magic_doorknob";

    @SuppressWarnings({"PublicField", "StaticNonFinalField", "NonConstantLogger"})
    public static final Logger LOGGER = LogManager.getLogger();

    public ModMagicDoorknob() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(BlockColorHandlers.class);
        modEventBus.register(Blocks.class);
        modEventBus.register(Items.class);
        modEventBus.register(ModelLoaders.class);
        modEventBus.register(TileEntities.class);
    }
}
