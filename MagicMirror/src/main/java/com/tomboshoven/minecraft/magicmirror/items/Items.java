package com.tomboshoven.minecraft.magicmirror.items;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

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
    public static final Item MAGIC_MIRROR = new BlockItem(
            Blocks.MAGIC_MIRROR,
            new Item.Properties().group(ItemGroup.DECORATIONS)
    ).setRegistryName(MagicMirrorMod.MOD_ID, "magic_mirror");

    private Items() {
    }

    @SuppressWarnings("BoundedWildcard")
    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        event.getRegistry().registerAll(MAGIC_MIRROR);
    }
}
