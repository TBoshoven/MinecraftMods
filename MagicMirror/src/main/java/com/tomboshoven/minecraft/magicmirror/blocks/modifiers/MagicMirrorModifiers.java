package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public final class MagicMirrorModifiers {

    /**
     * A registry of all modifiers.
     */
    public static final IForgeRegistry<MagicMirrorModifier> MAGIC_MIRROR_MODIFIER_REGISTRY = new RegistryBuilder<MagicMirrorModifier>().setName(new ResourceLocation(MagicMirrorMod.MOD_ID, "magic_mirror_modifiers")).setType(MagicMirrorModifier.class).create();

    /**
     * Deferred register of all builtin modifiers.
     */
    public static final DeferredRegister<MagicMirrorModifier> MAGIC_MIRROR_MODIFIERS = new DeferredRegister<>(MAGIC_MIRROR_MODIFIER_REGISTRY, MagicMirrorMod.MOD_ID);

    static {
        MAGIC_MIRROR_MODIFIERS.register("armor", ArmorMagicMirrorModifier::new);
        MAGIC_MIRROR_MODIFIERS.register("banner", BannerMagicMirrorModifier::new);
        MAGIC_MIRROR_MODIFIERS.register("creature", CreatureMagicMirrorModifier::new);
    }

    public static void register(IEventBus eventBus) {
        MAGIC_MIRROR_MODIFIERS.register(eventBus);
    }
}
