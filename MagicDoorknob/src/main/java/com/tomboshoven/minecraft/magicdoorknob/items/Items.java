package com.tomboshoven.minecraft.magicdoorknob.items;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicdoorknob.ModMagicDoorknob;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

/**
 * Collection of all items in the mod.
 */
@EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Items {
    /**
     * The magic doorknob items by type name.
     */
    public static final Map<String, ItemMagicDoorknob> itemDoorknobs = Maps.newHashMap();

    static {
        // Add all Vanilla tool materials
        addDoorknob("wood", ItemTier.WOOD, "planks_oak");
        addDoorknob("stone", ItemTier.STONE, "stone");
        addDoorknob("iron", ItemTier.IRON, "iron_block");
        addDoorknob("gold", ItemTier.GOLD, "gold_block");
        addDoorknob("diamond", ItemTier.DIAMOND, "diamond_block");
    }

    private Items() {
    }

    /**
     * Add a doorknob item.
     * <p>
     * Make sure to add a translation key.
     *
     * @param typeName    The type name of the item. Keep this stable, since it is used in NBT data.
     * @param tier        The material this doorknob is made of
     * @param mainTexture The main texture of the doorknob
     */
    private static void addDoorknob(String typeName, IItemTier tier, ResourceLocation mainTexture) {
        Item i = new ItemMagicDoorknob(typeName, material, mainTexture)
                .setRegistryName(ModMagicDoorknob.MOD_ID, String.format("magic_doorknob_%s", typeName))
                .setTranslationKey(String.format("%s.magic_doorknob.%s", ModMagicDoorknob.MOD_ID, typeName))
                .setCreativeTab(CreativeTabs.MISC);
        itemDoorknobs.put(typeName, (ItemMagicDoorknob) i);
    }

    /**
     * Convenience function for doorknobs using Vanilla materials.
     *
     * @param typeName  The type name of the item. Keep this stable, since it is used in NBT data.
     * @param tier      The material this doorknob is made of
     * @param blockName The name of the block that provides the texture of the doorknob
     */
    private static void addDoorknob(String typeName, IItemTier tier, String blockName) {
        addDoorknob(typeName, tier, new ResourceLocation("minecraft", String.format("blocks/%s", blockName)));
    }

    @SuppressWarnings("BoundedWildcard")
    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        itemDoorknobs.values().forEach(registry::register);
    }
}
