package com.tomboshoven.minecraft.magicdoorknob.items;

import com.tomboshoven.minecraft.magicdoorknob.ModMagicDoorknob;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all items in the mod.
 */
@EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Items {
    /**
     * The item version of the magic doorway block.
     */
    public static final Item itemMagicDoorknob = new ItemMagicDoorknob()
            .setRegistryName(ModMagicDoorknob.MOD_ID, "magic_doorknob")
            .setCreativeTab(CreativeTabs.MISC);

    private Items() {
    }

    @SuppressWarnings("BoundedWildcard")
    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        event.getRegistry().registerAll(itemMagicDoorknob);
    }
}
