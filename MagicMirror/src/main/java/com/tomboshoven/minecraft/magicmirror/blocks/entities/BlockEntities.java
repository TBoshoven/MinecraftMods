package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Collection of all block entities in the mod.
 */
public final class BlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MagicMirrorMod.MOD_ID);

    /**
     * Core that has all the actual logic.
     */
    public static final Supplier<BlockEntityType<MagicMirrorCoreBlockEntity>> MAGIC_MIRROR_CORE =
            BLOCK_ENTITIES.register("magic_mirror_core", () -> BlockEntityType.Builder.of(MagicMirrorCoreBlockEntity::new, Blocks.MAGIC_MIRROR_CORE.get()).build(null));
    /**
     * Parts that defer logic to the core.
     */
    public static final Supplier<BlockEntityType<MagicMirrorPartBlockEntity>> MAGIC_MIRROR_PART =
            BLOCK_ENTITIES.register("magic_mirror_part", () -> BlockEntityType.Builder.of(MagicMirrorPartBlockEntity::new, Blocks.MAGIC_MIRROR_PART.get()).build(null));

    private BlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
