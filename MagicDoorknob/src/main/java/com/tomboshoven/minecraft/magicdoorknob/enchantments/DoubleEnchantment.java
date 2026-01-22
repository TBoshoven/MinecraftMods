package com.tomboshoven.minecraft.magicdoorknob.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DoubleEnchantment extends Enchantment {
    protected DoubleEnchantment(Rarity rarity, EquipmentSlot... equipmentSlots) {
        super(rarity, EnchantmentCategory.DIGGER, equipmentSlots);
    }

    public int getMinCost(int val) {
        return 15;
    }

    public int getMaxCost(int val) {
        return super.getMinCost(val) + 50;
    }
}
