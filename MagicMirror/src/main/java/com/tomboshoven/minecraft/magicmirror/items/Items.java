package com.tomboshoven.minecraft.magicmirror.items;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all items in the mod.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Items {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicMirrorMod.MOD_ID);

    /**
     * The item version of the magic mirror block.
     */
    public static final RegistryObject<Item> MAGIC_MIRROR =
            ITEMS.register("magic_mirror", () -> new MagicMirrorBlockItem(
                    Blocks.MAGIC_MIRROR_INACTIVE.get(),
                    new Item.Properties()
            ));

    private Items() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(Items::registerCreativeTabs);
    }

    private static void registerCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (CreativeModeTabs.FUNCTIONAL_BLOCKS.equals(event.getTabKey())) {
            event.accept(MAGIC_MIRROR);
        }
    }
}
