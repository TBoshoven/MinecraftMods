package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class MagicMirrorModifiers {

    /**
     * The resource key for the modifier registry.
     */
    public static final ResourceKey<Registry<MagicMirrorModifier>> MAGIC_MIRROR_MODIFIER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MagicMirrorMod.MOD_ID, "magic_mirror_modifiers"));
    /**
     * A registry of all modifiers.
     */
    public static final Registry<MagicMirrorModifier> MAGIC_MIRROR_MODIFIER_REGISTRY = new RegistryBuilder<>(MAGIC_MIRROR_MODIFIER_REGISTRY_KEY).create();

    /**
     * Deferred register of al builtin modifiers.
     */
    public static final DeferredRegister<MagicMirrorModifier> MAGIC_MIRROR_MODIFIERS = DeferredRegister.create(MAGIC_MIRROR_MODIFIER_REGISTRY, MagicMirrorMod.MOD_ID);

    static {
        MAGIC_MIRROR_MODIFIERS.register("armor", ArmorMagicMirrorModifier::new);
        MAGIC_MIRROR_MODIFIERS.register("banner", BannerMagicMirrorModifier::new);
        MAGIC_MIRROR_MODIFIERS.register("creature", CreatureMagicMirrorModifier::new);
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(MagicMirrorModifiers::registerRegistries);
        MAGIC_MIRROR_MODIFIERS.register(eventBus);
    }

    static void registerRegistries(NewRegistryEvent event) {
        event.register(MAGIC_MIRROR_MODIFIER_REGISTRY);
    }
}
