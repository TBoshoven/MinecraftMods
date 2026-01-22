package com.tomboshoven.minecraft.magicdoorknob.enchantments;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.data.ItemTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Collection of all enchantments in the mod.
 */
public final class Enchantments {
    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, MagicDoorknobMod.MOD_ID);
    /**
     * The "double" enchantment, which adds a secondary door at the end of the doorway.
     */
    public static final Supplier<Enchantment> DOUBLE = ENCHANTMENTS.register(
            "double",
            () -> new Enchantment(
                    Enchantment.definition(
                            ItemTags.MAGIC_DOORKNOB,
                            1,
                            1,
                            Enchantment.dynamicCost(15, 0),
                            Enchantment.dynamicCost(65, 0),
                            8,
                            EquipmentSlot.MAINHAND
                    )
            )
    );

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
