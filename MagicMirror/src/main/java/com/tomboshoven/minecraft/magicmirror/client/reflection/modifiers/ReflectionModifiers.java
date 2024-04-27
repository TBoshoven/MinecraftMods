package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * Collection of all available reflection modifiers.
 */
public class ReflectionModifiers {
    public static final ResourceKey<Registry<ReflectionModifier>> REFLECTION_MODIFIER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(MagicMirrorMod.MOD_ID, "reflection_modifiers"));
    public static final Registry<ReflectionModifier> REFLECTION_MODIFIER_REGISTRY = new RegistryBuilder<>(REFLECTION_MODIFIER_REGISTRY_KEY).create();

    public static final DeferredRegister<ReflectionModifier> REFLECTION_MODIFIERS = DeferredRegister.create(REFLECTION_MODIFIER_REGISTRY, MagicMirrorMod.MOD_ID);

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
}
