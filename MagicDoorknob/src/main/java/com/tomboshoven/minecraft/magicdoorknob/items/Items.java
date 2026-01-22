package com.tomboshoven.minecraft.magicdoorknob.items;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Collection of all items in the mod.
 */
public final class Items {
    /**
     * The magic doorknob items by type name.
     */
    public static final Map<String, MagicDoorknobItem> DOORKNOBS = Maps.newHashMap();

    private Items() {
    }

    /**
     * Add a doorknob item.
     * <p>
     * Make sure to add a translation key.
     *
     * @param registry    The registry to which to add the doorknob item.
     * @param typeName    The type name of the item. Keep this stable, since it is used in NBT data.
     * @param tier        The material this doorknob is made of
     * @param mainTexture The main texture of the doorknob
     * @param ingredient  The ingredient used to build the doorknob
     * @param netheriteSmithingBase The base item to use to create this one using netherite smithing
     */
    private static Item addDoorknob(IForgeRegistry<? super Item> registry, String typeName, IItemTier tier, ResourceLocation mainTexture, @Nullable Supplier<Ingredient> ingredient, Item netheriteSmithingBase) {
        MagicDoorknobItem i = new MagicDoorknobItem(new Item.Properties().tab(ItemGroup.TAB_TOOLS), typeName, tier, mainTexture, ingredient, netheriteSmithingBase);
        i.setRegistryName(MagicDoorknobMod.MOD_ID, String.format("magic_doorknob_%s", typeName));
        registry.register(i);
        DOORKNOBS.put(typeName, i);
        return i;
    }

    /**
     * Convenience function for doorknobs using Vanilla materials.
     *
     * @param typeName     The type name of the item. Keep this stable, since it is used in NBT data.
     * @param tier       The material this doorknob is made of
     * @param blockName    The name of the block that provides the texture of the doorknob
     */
    private static Item addDoorknob(IForgeRegistry<? super Item> registry, String typeName, ItemTier tier, String blockName) {
        return addDoorknob(registry, typeName, tier, new ResourceLocation(String.format("block/%s", blockName)), tier::getRepairIngredient, null);
    }

    /**
     * Convenience function for doorknobs using Vanilla materials.
     *
     * @param registry   The registry to which to add the doorknob item.
     * @param typeName   The type name of the item. Keep this stable, since it is used in NBT data.
     * @param tier       The material this doorknob is made of
     * @param blockName  The name of the block that provides the texture of the doorknob
     * @param netheriteSmithingBase The base item to use to create this one using netherite smithing
     */
    private static Item addDoorknob(IForgeRegistry<? super Item> registry, String typeName, IItemTier tier, String blockName, Item netheriteSmithingBase) {
        return addDoorknob(registry, typeName, tier, new ResourceLocation("minecraft", String.format("block/%s", blockName)), null, netheriteSmithingBase);
    }

    public static void register(IEventBus eventBus) {
        eventBus.addGenericListener(Item.class, Items::registerItems);
    }

    private static void registerItems(Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // Add all Vanilla tool materials
        addDoorknob(registry, "wood", ItemTier.WOOD, "oak_planks");
        addDoorknob(registry, "stone", ItemTier.STONE, "stone");
        addDoorknob(registry, "iron", ItemTier.IRON, "iron_block");
        addDoorknob(registry, "gold", ItemTier.GOLD, "gold_block");
        Item diamondDoorknob = addDoorknob(registry, "diamond", ItemTier.DIAMOND, "diamond_block");
        addDoorknob(registry, "netherite", ItemTier.NETHERITE, "netherite_block", diamondDoorknob);
    }
}
