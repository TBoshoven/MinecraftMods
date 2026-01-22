package com.tomboshoven.minecraft.magicdoorknob.enchantments;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * Collection of all enchantments in the mod.
 */
public final class Enchantments {
    private static final DeferredRegister<Enchantment> ENCHANTMENTS = new DeferredRegister<>(ForgeRegistries.ENCHANTMENTS, MagicDoorknobMod.MOD_ID);
    /**
     * The "double" enchantment, which adds a secondary door at the end of the doorway.
     */
    public static final Supplier<Enchantment> DOUBLE = ENCHANTMENTS.register(
            "double",
            () -> new DoubleEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND)
    );

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
