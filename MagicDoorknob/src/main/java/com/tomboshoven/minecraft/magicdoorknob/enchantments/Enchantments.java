package com.tomboshoven.minecraft.magicdoorknob.enchantments;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Collection of all enchantments in the mod.
 */
public final class Enchantments {
    // In some registration class
    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENT_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, MagicDoorknobMod.MOD_ID);

    /**
     * The "double" enchantment, which adds a secondary door at the end of the doorway.
     */
    public static final ResourceKey<Enchantment> DOUBLE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(MagicDoorknobMod.MOD_ID, "double"));

    /**
     * The component type for the "double" enchantment.
     */
    public static final Supplier<DataComponentType<DoubleEnchantment>> DOUBLE_COMPONENT =
            ENCHANTMENT_COMPONENT_TYPES.register(DOUBLE.location().getPath(), () -> DataComponentType.<DoubleEnchantment>builder().persistent(DoubleEnchantment.CODEC).build());

    public static void register(IEventBus eventBus) {
        ENCHANTMENT_COMPONENT_TYPES.register(eventBus);
    }
}
