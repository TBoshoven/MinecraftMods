package com.tomboshoven.minecraft.magicmirror.items;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Collection of all items in the mod.
 */
@EventBusSubscriber
public final class Items {
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MagicMirrorMod.MOD_ID);

    /**
     * The item version of the magic mirror block.
     */
    public static final RegistryObject<Item> MAGIC_MIRROR =
            ITEMS.register("magic_mirror", () -> new BlockItem(
                    Blocks.MAGIC_MIRROR.get(),
                    new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)
            ));

    private Items() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
