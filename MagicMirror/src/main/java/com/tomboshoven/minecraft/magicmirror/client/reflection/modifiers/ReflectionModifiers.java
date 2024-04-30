package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Collection of all available reflection modifiers.
 */
public class ReflectionModifiers {
    /**
     * A registry of all modifiers.
     */
    public static final IForgeRegistry<ReflectionModifier> REFLECTION_MODIFIER_REGISTRY = new RegistryBuilder<ReflectionModifier>().setName(new ResourceLocation(MagicMirrorMod.MOD_ID, "reflection_modifiers")).setType(ReflectionModifier.class).create();

    /**
     * Deferred register of all builtin modifiers.
     */
    public static final DeferredRegister<ReflectionModifier> REFLECTION_MODIFIERS = new DeferredRegister<>(REFLECTION_MODIFIER_REGISTRY, MagicMirrorMod.MOD_ID);

    private static final Map<MagicMirrorModifier, ReflectionModifier> REFLECTION_MAPPING = new WeakHashMap<>();

    static {
        REFLECTION_MODIFIERS.register("armor", ArmorReflectionModifier::new);
        REFLECTION_MODIFIERS.register("banner", BannerReflectionModifier::new);
        REFLECTION_MODIFIERS.register("creature", CreatureReflectionModifier::new);
    }

    public static void register(IEventBus eventBus) {
        REFLECTION_MODIFIERS.register(eventBus);
    }

    /**
     * @param modifier The modifier for which to find the reflection modifier.
     *
     * @return The reflection modifier for that modifier, or null if there is none.
     */
    @Nullable
    public static ReflectionModifier forMirrorModifier(MagicMirrorModifier modifier) {
        return REFLECTION_MAPPING.computeIfAbsent(modifier, m -> {
            // Current convention is reflection modifiers have the same location as the mirror modifiers.
            return REFLECTION_MODIFIER_REGISTRY.getValue(m.getRegistryName());
        });
    }
}
