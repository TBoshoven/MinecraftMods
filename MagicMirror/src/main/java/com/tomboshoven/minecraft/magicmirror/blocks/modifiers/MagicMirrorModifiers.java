package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class MagicMirrorModifiers {

    /**
     * Deferred register of all builtin modifiers.
     */
    public static final DeferredRegister<MagicMirrorModifier> MAGIC_MIRROR_MODIFIERS = DeferredRegister.create(MagicMirrorModifier.class, MagicMirrorMod.MOD_ID);

    /**
     * A registry of all modifiers.
     */
    public static final Supplier<IForgeRegistry<MagicMirrorModifier>> MAGIC_MIRROR_MODIFIER_REGISTRY = MAGIC_MIRROR_MODIFIERS.makeRegistry("magic_mirror_modifiers", RegistryBuilder::new);

    static {
        MAGIC_MIRROR_MODIFIERS.register("armor", ArmorMagicMirrorModifier::new);
        MAGIC_MIRROR_MODIFIERS.register("banner", BannerMagicMirrorModifier::new);
        MAGIC_MIRROR_MODIFIERS.register("creature", CreatureMagicMirrorModifier::new);
    }

    public static void register(IEventBus eventBus) {
        MAGIC_MIRROR_MODIFIERS.register(eventBus);
    }
}
