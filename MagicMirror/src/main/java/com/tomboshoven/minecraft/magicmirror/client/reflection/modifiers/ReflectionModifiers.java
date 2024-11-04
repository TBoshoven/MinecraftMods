package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifiers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Collection of all available reflection modifiers.
 */
public class ReflectionModifiers {
    /**
     * The resource key for the modifier registry.
     */
    public static final ResourceKey<Registry<ReflectionModifier>> REFLECTION_MODIFIER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MagicMirrorMod.MOD_ID, "reflection_modifiers"));
    /**
     * A registry of all modifiers.
     */
    public static final Registry<ReflectionModifier> REFLECTION_MODIFIER_REGISTRY = new RegistryBuilder<>(REFLECTION_MODIFIER_REGISTRY_KEY).create();

    /**
     * Deferred register of al builtin modifiers.
     */
    public static final DeferredRegister<ReflectionModifier> REFLECTION_MODIFIERS = DeferredRegister.create(REFLECTION_MODIFIER_REGISTRY, MagicMirrorMod.MOD_ID);

    private static final Map<MagicMirrorModifier, ReflectionModifier> REFLECTION_MAPPING = new WeakHashMap<>();

    static {
        REFLECTION_MODIFIERS.register("armor", ArmorReflectionModifier::new);
        REFLECTION_MODIFIERS.register("banner", BannerReflectionModifier::new);
        REFLECTION_MODIFIERS.register("creature", CreatureReflectionModifier::new);
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ReflectionModifiers::registerRegistries);
        REFLECTION_MODIFIERS.register(eventBus);
    }

    static void registerRegistries(NewRegistryEvent event) {
        event.register(REFLECTION_MODIFIER_REGISTRY);
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
            ResourceLocation key = MagicMirrorModifiers.MAGIC_MIRROR_MODIFIER_REGISTRY.getKey(m);
            return REFLECTION_MODIFIER_REGISTRY.getValue(key);
        });
    }
}
