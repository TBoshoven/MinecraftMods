package com.tomboshoven.minecraft.magicmirror.items;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Collection of all items in the mod.
 */
public final class Items {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MagicMirrorMod.MOD_ID);

    /**
     * The item version of the magic mirror block.
     */
    public static final DeferredItem<BlockItem> MAGIC_MIRROR =
            ITEMS.registerItem("magic_mirror", props -> new MagicMirrorBlockItem(Blocks.MAGIC_MIRROR_INACTIVE.get(), props));

    private Items() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(Items::registerCreativeTabs);
    }

    private static void registerCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (CreativeModeTabs.FUNCTIONAL_BLOCKS.equals(event.getTabKey())) {
            event.accept(MAGIC_MIRROR.get());
        }
    }
}
