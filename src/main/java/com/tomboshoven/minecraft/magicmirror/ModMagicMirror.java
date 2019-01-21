package com.tomboshoven.minecraft.magicmirror;

import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirror;
import com.tomboshoven.minecraft.magicmirror.renderers.TileEntityMagicMirrorRenderer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod(modid = ModMagicMirror.MOD_ID, version = ModMagicMirror.VERSION, name = ModMagicMirror.NAME)
@Mod.EventBusSubscriber()
public class ModMagicMirror {
    @SuppressWarnings("WeakerAccess")
    public static final String MOD_ID = "magic_mirror";
    @SuppressWarnings("WeakerAccess")
    public static final String VERSION = "0.1";
    @SuppressWarnings("WeakerAccess")
    public static final String NAME = "Magic Mirror";

    public static Logger logger;

    private Block blockMagicMirror;
    private Item itemBlockMagicMirror;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);

        blockMagicMirror = new BlockMagicMirror()
                .setRegistryName(MOD_ID, "magic_mirror")
                .setTranslationKey(String.format("%s.magic_mirror", MOD_ID))
                .setCreativeTab(CreativeTabs.MISC);
        itemBlockMagicMirror = new ItemBlock(blockMagicMirror)
                .setRegistryName(MOD_ID, "magic_mirror")
                .setCreativeTab(CreativeTabs.MISC);

        GameRegistry.registerTileEntity(TileEntityMagicMirror.class, new ResourceLocation(MOD_ID, "magic_mirror"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMagicMirror.class, new TileEntityMagicMirrorRenderer());
    }

    @Mod.EventHandler
    public void init(FMLPostInitializationEvent event) {
        logger.info("{} version {} is initialized", NAME, VERSION);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        logger.info("Registering blocks");
        event.getRegistry().registerAll(blockMagicMirror);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        logger.info("Registering items");
        event.getRegistry().registerAll(itemBlockMagicMirror);
    }

    @SubscribeEvent
    public void registerRenders(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(itemBlockMagicMirror, 0, new ModelResourceLocation(new ResourceLocation(MOD_ID, "magic_mirror"), "inventory"));
    }
}
