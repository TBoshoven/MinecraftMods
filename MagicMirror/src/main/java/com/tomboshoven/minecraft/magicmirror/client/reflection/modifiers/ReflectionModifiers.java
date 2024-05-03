package com.tomboshoven.minecraft.magicmirror.client.reflection.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * Collection of all available reflection modifiers.
 */
public class ReflectionModifiers {
    /**
     * Deferred register of all builtin modifiers.
     */
    public static final DeferredRegister<ReflectionModifier> REFLECTION_MODIFIERS = DeferredRegister.create(ReflectionModifier.class, MagicMirrorMod.MOD_ID);

    /**
     * A registry of all modifiers.
     */
    public static final Supplier<IForgeRegistry<ReflectionModifier>> REFLECTION_MODIFIER_REGISTRY = REFLECTION_MODIFIERS.makeRegistry("reflection_modifiers", RegistryBuilder::new);

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
            return REFLECTION_MODIFIER_REGISTRY.get().getValue(m.getRegistryName());
        });
    }
}