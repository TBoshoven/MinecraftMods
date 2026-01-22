package com.tomboshoven.minecraft.magicdoorknob.enchantments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Data contained by the "double" enchantment.
 */
public record DoubleEnchantment() {
    public static final Codec<DoubleEnchantment> CODEC = RecordCodecBuilder.create(instance ->
            instance.point(new DoubleEnchantment())
    );
}
