package com.tomboshoven.minecraft.magicdoorknob.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class DoubleEnchantment extends Enchantment {
    protected DoubleEnchantment(Rarity rarity, EquipmentSlotType... equipmentSlots) {
        super(rarity, EnchantmentType.DIGGER, equipmentSlots);
    }

    public int getMinCost(int val) {
        return 15;
    }

    public int getMaxCost(int val) {
        return super.getMinCost(val) + 50;
    }
}
