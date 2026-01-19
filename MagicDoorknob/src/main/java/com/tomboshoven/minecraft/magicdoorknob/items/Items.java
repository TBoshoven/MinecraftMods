package com.tomboshoven.minecraft.magicdoorknob.items;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
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
    private static Item addDoorknob(IForgeRegistry<? super Item> registry, String typeName, Tier tier, ResourceLocation mainTexture, @Nullable Supplier<Ingredient> ingredient, Item netheriteSmithingBase) {
        MagicDoorknobItem i = new MagicDoorknobItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS), typeName, tier, mainTexture, ingredient, netheriteSmithingBase);
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
    private static Item addDoorknob(IForgeRegistry<? super Item> registry, String typeName, Tier tier, String blockName) {
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
    private static Item addDoorknob(IForgeRegistry<? super Item> registry, String typeName, Tier tier, String blockName, Item netheriteSmithingBase) {
        return addDoorknob(registry, typeName, tier, new ResourceLocation("minecraft", String.format("block/%s", blockName)), null, netheriteSmithingBase);
    }

    public static void register(IEventBus eventBus) {
        eventBus.addGenericListener(Item.class, Items::registerItems);
    }

    private static void registerItems(Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // Add all Vanilla tool materials
        addDoorknob(registry, "wood", Tiers.WOOD, "oak_planks");
        addDoorknob(registry, "stone", Tiers.STONE, "stone");
        addDoorknob(registry, "iron", Tiers.IRON, "iron_block");
        addDoorknob(registry, "gold", Tiers.GOLD, "gold_block");
        Item diamondDoorknob = addDoorknob(registry, "diamond", Tiers.DIAMOND, "diamond_block");
        addDoorknob(registry, "netherite", Tiers.NETHERITE, "netherite_block", diamondDoorknob);
    }
}
