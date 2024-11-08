package com.tomboshoven.minecraft.magicdoorknob.items;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Collection of all items in the mod.
 */
public final class Items {
    /**
     * The magic doorknob items by type name.
     */
    public static final Map<String, DeferredItem<MagicDoorknobItem>> DOORKNOBS = Maps.newLinkedHashMap();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MagicDoorknobMod.MOD_ID);

    private Items() {
    }

    /**
     * Add a doorknob item.
     * <p>
     * Make sure to add a translation key.
     *
     * @param typeName     The type name of the item. Keep this stable, since it is used in NBT data.
     * @param toolMaterial The material this doorknob is made of
     * @param mainTexture  The main texture of the doorknob
     * @param ingredients  The ingredients used to build the doorknob
     */
    private static void addDoorknob(String typeName, ToolMaterial toolMaterial, ResourceLocation mainTexture, Supplier<TagKey<Item>> ingredients) {
        DeferredItem<MagicDoorknobItem> item = ITEMS.registerItem(String.format("magic_doorknob_%s", typeName), (Item.Properties properties) -> new MagicDoorknobItem(properties, typeName, toolMaterial, mainTexture, ingredients));
        DOORKNOBS.put(typeName, item);
    }

    /**
     * Convenience function for doorknobs using Vanilla materials.
     *
     * @param typeName     The type name of the item. Keep this stable, since it is used in NBT data.
     * @param toolMaterial The material this doorknob is made of
     * @param blockName    The name of the block that provides the texture of the doorknob
     */
    private static void addDoorknob(String typeName, ToolMaterial toolMaterial, String blockName) {
        addDoorknob(typeName, toolMaterial, ResourceLocation.withDefaultNamespace(String.format("block/%s", blockName)), toolMaterial::repairItems);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(Items::registerCreativeTabs);
    }

    private static void registerCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (CreativeModeTabs.TOOLS_AND_UTILITIES.equals(event.getTabKey())) {
            DOORKNOBS.values().forEach(event::accept);
        }
    }

    static {
        // Add all Vanilla tool materials
        addDoorknob("wood", ToolMaterial.WOOD, "oak_planks");
        addDoorknob("stone", ToolMaterial.STONE, "stone");
        addDoorknob("iron", ToolMaterial.IRON, "iron_block");
        addDoorknob("gold", ToolMaterial.GOLD, "gold_block");
        addDoorknob("diamond", ToolMaterial.DIAMOND, "diamond_block");
        addDoorknob("netherite", ToolMaterial.NETHERITE, "netherite_block");
    }
}
