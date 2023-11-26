package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all blocks in the mod.
 */
@EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Blocks {
    /**
     * A magic mirror block. Shows you your reflection, and more!
     */
    public static final Block blockMagicMirror = new BlockMagicMirror()
            .setRegistryName(ModMagicMirror.MOD_ID, "magic_mirror")
            .setUnlocalizedName(String.format("%s.magic_mirror", ModMagicMirror.MOD_ID))
            .setCreativeTab(CreativeTabs.MISC);

    private Blocks() {
    }

    @SuppressWarnings("BoundedWildcard")
    @SubscribeEvent
    public static void registerBlocks(Register<Block> event) {
        event.getRegistry().registerAll(blockMagicMirror);
    }
}
