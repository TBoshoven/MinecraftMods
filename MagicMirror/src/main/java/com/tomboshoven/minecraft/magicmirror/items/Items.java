package com.tomboshoven.minecraft.magicmirror.items;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Collection of all items in the mod.
 */
@EventBusSubscriber
public final class Items {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicMirrorMod.MOD_ID);

    /**
     * The item version of the magic mirror block.
     */
    public static final RegistryObject<Item> MAGIC_MIRROR =
            ITEMS.register("magic_mirror", () -> new MagicMirrorBlockItem(
                    Blocks.MAGIC_MIRROR_INACTIVE.get(),
                    new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
            ));

    private Items() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
