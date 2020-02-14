package com.tomboshoven.minecraft.magicdoorknob;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.colorhandlers.BlockColorHandlers;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoor;
import com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities.TileEntityMagicDoorway;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.modelloaders.ModelLoaders;
import com.tomboshoven.minecraft.magicdoorknob.models.Models;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(modid = ModMagicDoorknob.MOD_ID, name = ModMagicDoorknob.NAME, useMetadata = true)
public class ModMagicDoorknob {
    public static final String MOD_ID = "magic_doorknob";
    static final String NAME = "Magic Doorknob";

    @SuppressWarnings({"PublicField", "StaticNonFinalField", "NonConstantLogger"})
    public static Logger logger;

    @SuppressWarnings("MethodMayBeStatic")
    @EventHandler
    public void init(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(Blocks.class);
        MinecraftForge.EVENT_BUS.register(Items.class);
        MinecraftForge.EVENT_BUS.register(ModelLoaders.class);
        MinecraftForge.EVENT_BUS.register(Models.class);

        // Register tile entities
        GameRegistry.registerTileEntity(TileEntityMagicDoorway.class, new ResourceLocation(MOD_ID, "magic_doorway"));
        GameRegistry.registerTileEntity(TileEntityMagicDoor.class, new ResourceLocation(MOD_ID, "magic_door"));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        BlockColorHandlers.registerColorHandlers();
    }
}
