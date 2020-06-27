package com.tomboshoven.minecraft.magicdoorknob.items;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
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
    public static final Map<String, MagicDoorknobItem> DOORKNOBS = Maps.newHashMap();

    private Items() {
    }

    /**
     * Add a doorknob item.
     * <p>
     * Make sure to add a translation key.
     *
     * @param registry  The registry to which to add the doorknob item.
     * @param typeName    The type name of the item. Keep this stable, since it is used in NBT data.
     * @param tier        The material this doorknob is made of
     * @param mainTexture The main texture of the doorknob
     * @param recipeTag   The tag to use in recipes
     */
    private static void addDoorknob(IForgeRegistry<? super Item> registry, String typeName, IItemTier tier, ResourceLocation mainTexture, Tag<Item> recipeTag) {
        MagicDoorknobItem i = new MagicDoorknobItem(new Item.Properties().group(ItemGroup.TOOLS), typeName, tier, mainTexture, recipeTag);
        i.setRegistryName(MagicDoorknobMod.MOD_ID, String.format("magic_doorknob_%s", typeName));
        registry.register(i);
        DOORKNOBS.put(typeName, i);
    }

    /**
     * Convenience function for doorknobs using Vanilla materials.
     *
     * @param registry  The registry to which to add the doorknob item.
     * @param typeName  The type name of the item. Keep this stable, since it is used in NBT data.
     * @param tier      The material this doorknob is made of
     * @param blockName The name of the block that provides the texture of the doorknob
     * @param recipeTag The tag to use in recipes
     */
    private static void addDoorknob(IForgeRegistry<? super Item> registry, String typeName, IItemTier tier, String blockName, Tag<Item> recipeTag) {
        addDoorknob(registry, typeName, tier, new ResourceLocation("minecraft", String.format("block/%s", blockName)), recipeTag);
    }

    public static void register(IEventBus eventBus) {
        eventBus.addGenericListener(Item.class, Items::registerItems);
    }

    private static void registerItems(Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // Add all Vanilla tool materials
        addDoorknob(registry, "wood", ItemTier.WOOD, "oak_planks", ItemTags.PLANKS);
        addDoorknob(registry, "stone", ItemTier.STONE, "stone", Tags.Items.COBBLESTONE);
        addDoorknob(registry, "iron", ItemTier.IRON, "iron_block", Tags.Items.INGOTS_IRON);
        addDoorknob(registry, "gold", ItemTier.GOLD, "gold_block", Tags.Items.INGOTS_GOLD);
        addDoorknob(registry, "diamond", ItemTier.DIAMOND, "diamond_block", Tags.Items.GEMS_DIAMOND);
    }
}
