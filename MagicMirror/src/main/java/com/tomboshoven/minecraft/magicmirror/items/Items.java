package com.tomboshoven.minecraft.magicmirror.items;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Collection of all items in the mod.
 */
@EventBusSubscriber
public final class Items {
    /**
     * The item version of the magic mirror block.
     */
    public static final Item itemBlockMagicMirror = new ItemBlock(Blocks.blockMagicMirror)
            .setRegistryName(ModMagicMirror.MOD_ID, "magic_mirror")
            .setCreativeTab(CreativeTabs.MISC);

    private Items() {
    }

    @SuppressWarnings("BoundedWildcard")
    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        event.getRegistry().registerAll(itemBlockMagicMirror);
    }
}
