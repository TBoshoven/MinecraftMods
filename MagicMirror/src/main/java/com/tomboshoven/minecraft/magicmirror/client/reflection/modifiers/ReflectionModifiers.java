package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.ArmorMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.BannerMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.CreatureMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.DyeMagicMirrorModifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Collection of all available reflection modifiers.
 */
public class ReflectionModifiers {
    /**
     * Map from modifier names to reflection modifier instances.
     */
    public static final Map<String, ReflectionModifier> MODIFIERS = new HashMap<>();

    static {
        MODIFIERS.put(ArmorMagicMirrorModifier.NAME, new ArmorReflectionModifier());
        MODIFIERS.put(BannerMagicMirrorModifier.NAME, new BannerReflectionModifier());
        MODIFIERS.put(CreatureMagicMirrorModifier.NAME, new CreatureReflectionModifier());
        MODIFIERS.put(DyeMagicMirrorModifier.NAME, new DyeReflectionModifier());
    }
}
