package com.tomboshoven.minecraft.magicdoorknob.items;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicdoorknob.ModMagicDoorknob;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;

/**
 * Collection of all items in the mod.
 */
@EventBusSubscriber
public final class Items {
    /**
     * The magic doorknob items by type name.
     */
    public static final Map<String, ItemMagicDoorknob> itemDoorknobs = Maps.newHashMap();

    static {
        // Add all Vanilla tool materials
        addDoorknob("wood", Item.ToolMaterial.WOOD, "planks_oak");
        addDoorknob("stone", Item.ToolMaterial.STONE, "stone");
        addDoorknob("iron", Item.ToolMaterial.IRON, "iron_block");
        addDoorknob("gold", Item.ToolMaterial.GOLD, "gold_block");
        addDoorknob("diamond", Item.ToolMaterial.DIAMOND, "diamond_block");
    }

    private Items() {
    }

    /**
     * Add a doorknob item.
     * <p>
     * Make sure to add a translation key.
     *
     * @param typeName    The type name of the item. Keep this stable, since it is used in NBT data.
     * @param material    The material this doorknob is made of
     * @param mainTexture The main texture of the doorknob
     */
    private static void addDoorknob(String typeName, Item.ToolMaterial material, ResourceLocation mainTexture) {
        Item i = new ItemMagicDoorknob(typeName, material, mainTexture)
                .setRegistryName(ModMagicDoorknob.MOD_ID, String.format("magic_doorknob_%s", typeName))
                .setUnlocalizedName(String.format("%s.magic_doorknob.%s", ModMagicDoorknob.MOD_ID, typeName))
                .setCreativeTab(CreativeTabs.MISC);
        itemDoorknobs.put(typeName, (ItemMagicDoorknob) i);
    }

    /**
     * Convenience function for doorknobs using Vanilla materials.
     *
     * @param typeName  The type name of the item. Keep this stable, since it is used in NBT data.
     * @param material  The material this doorknob is made of
     * @param blockName The name of the block that provides the texture of the doorknob
     */
    private static void addDoorknob(String typeName, Item.ToolMaterial material, String blockName) {
        addDoorknob(typeName, material, new ResourceLocation("minecraft", String.format("blocks/%s", blockName)));
    }

    @SuppressWarnings("BoundedWildcard")
    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        itemDoorknobs.values().forEach(registry::register);
    }
}
