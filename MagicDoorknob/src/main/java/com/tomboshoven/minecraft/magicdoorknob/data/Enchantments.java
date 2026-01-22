package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.enchantments.DoubleEnchantment;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Optional;

import static com.tomboshoven.minecraft.magicdoorknob.enchantments.Enchantments.DOUBLE;
import static com.tomboshoven.minecraft.magicdoorknob.enchantments.Enchantments.DOUBLE_COMPONENT;

public class Enchantments {
    /**
     * @return A registry set builder for the enchantments.
     */
    static RegistrySetBuilder builder() {
        return new RegistrySetBuilder()
                .add(
                        Registries.ENCHANTMENT,
                        bootstrap -> bootstrap.register(
                                DOUBLE,
                                new Enchantment(
                                        Component.literal("Double"),
                                        new Enchantment.EnchantmentDefinition(
                                                bootstrap.registryLookup(Registries.ITEM).orElseThrow().getOrThrow(ItemTags.MAGIC_DOORKNOB),
                                                Optional.empty(),
                                                1,
                                                1,
                                                Enchantment.dynamicCost(15, 0),
                                                Enchantment.dynamicCost(65, 0),
                                                8,
                                                List.of(EquipmentSlotGroup.MAINHAND)
                                        ),
                                        HolderSet.empty(),
                                        DataComponentMap.builder().set(DOUBLE_COMPONENT, new DoubleEnchantment()).build()
                                )
                        )
                );
    }
}
