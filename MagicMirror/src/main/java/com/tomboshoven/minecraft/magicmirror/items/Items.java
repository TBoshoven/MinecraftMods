package com.tomboshoven.minecraft.magicmirror.items;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all items in the mod.
 */
@EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Items {
    /**
     * The item version of the magic mirror block.
     */
    public static final Item itemBlockMagicMirror = new BlockItem(Blocks.MAGIC_MIRROR)
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
