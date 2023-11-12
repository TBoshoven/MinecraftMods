package com.tomboshoven.minecraft.magicdoorknob.items;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Collection of all items in the mod.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Items {
    /**
     * The magic doorknob items by type name.
     */
    public static final Map<String, RegistryObject<MagicDoorknobItem>> DOORKNOBS = Maps.newLinkedHashMap();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicDoorknobMod.MOD_ID);

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
     * @param ingredient  The ingredient used to build the doorknob
     */
    private static void addDoorknob(String typeName, Tier tier, ResourceLocation mainTexture, Supplier<Ingredient> ingredient) {
        RegistryObject<MagicDoorknobItem> item = ITEMS.register(String.format("magic_doorknob_%s", typeName), () -> new MagicDoorknobItem(new Item.Properties(), typeName, tier, mainTexture, ingredient));
        DOORKNOBS.put(typeName, item);
    }

    /**
     * Convenience function for doorknobs using Vanilla materials.
     *
     * @param typeName   The type name of the item. Keep this stable, since it is used in NBT data.
     * @param tier       The material this doorknob is made of
     * @param blockName  The name of the block that provides the texture of the doorknob
     * @param ingredient The ingredient used to build the doorknob
     */
    private static void addDoorknob(String typeName, Tier tier, String blockName, Supplier<Ingredient> ingredient) {
        addDoorknob(typeName, tier, new ResourceLocation("minecraft", String.format("block/%s", blockName)), ingredient);
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
        addDoorknob("wood", Tiers.WOOD, "oak_planks", () -> Ingredient.of(ItemTags.PLANKS));
        addDoorknob("stone", Tiers.STONE, "stone", () -> Ingredient.of(Tags.Items.COBBLESTONE));
        addDoorknob("iron", Tiers.IRON, "iron_block", () -> Ingredient.of(Tags.Items.INGOTS_IRON));
        addDoorknob("gold", Tiers.GOLD, "gold_block", () -> Ingredient.of(Tags.Items.INGOTS_GOLD));
        addDoorknob("diamond", Tiers.DIAMOND, "diamond_block", () -> Ingredient.of(Tags.Items.GEMS_DIAMOND));
        addDoorknob("netherite", Tiers.NETHERITE, "netherite_block", () -> Ingredient.of(Tags.Items.INGOTS_NETHERITE));
    }
}
