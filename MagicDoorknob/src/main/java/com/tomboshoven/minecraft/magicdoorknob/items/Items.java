package com.tomboshoven.minecraft.magicdoorknob.items;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicdoorknob.ModMagicDoorknob;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Collection of all items in the mod.
 */
@EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Items {
    /**
     * The magic doorknob items.
     */
    public static final List<Item> itemDoorknobs = Lists.newArrayList();

    static void addDoorknob(String name, Item.ToolMaterial material, ResourceLocation mainTexture) {
        Item i = new ItemMagicDoorknob(material, mainTexture)
                .setRegistryName(ModMagicDoorknob.MOD_ID, String.format("magic_doorknob_%s", name))
                .setTranslationKey(String.format("%s.magic_doorknob.%s", ModMagicDoorknob.MOD_ID, name))
                .setCreativeTab(CreativeTabs.MISC);
        itemDoorknobs.add(i);
    }

    static void addDoorknob(String name, Item.ToolMaterial material, String blockName) {
        addDoorknob(name, material, new ResourceLocation("minecraft", String.format("blocks/%s", blockName)));
    }

    static {
        addDoorknob("wood", Item.ToolMaterial.WOOD, "planks_oak");
        addDoorknob("stone", Item.ToolMaterial.STONE, "stone");
        addDoorknob("iron", Item.ToolMaterial.IRON, "iron_block");
        addDoorknob("gold", Item.ToolMaterial.GOLD, "gold_block");
        addDoorknob("diamond", Item.ToolMaterial.DIAMOND, "diamond_block");
    }

    private Items() {
    }

    @SuppressWarnings("BoundedWildcard")
    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        itemDoorknobs.forEach(registry::register);
    }
}
