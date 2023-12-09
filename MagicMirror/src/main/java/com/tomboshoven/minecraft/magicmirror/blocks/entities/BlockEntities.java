package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Collection of all block entities in the mod.
 */
public final class BlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MagicMirrorMod.MOD_ID);

    /**
     * Core that has all the actual logic.
     */
    public static final RegistryObject<BlockEntityType<MagicMirrorCoreBlockEntity>> MAGIC_MIRROR_CORE =
            BLOCK_ENTITIES.register("magic_mirror_core", () -> BlockEntityType.Builder.of(MagicMirrorCoreBlockEntity::new, Blocks.MAGIC_MIRROR_CORE.get()).build(null));
    /**
     * Parts that defer logic to the core.
     */
    public static final RegistryObject<BlockEntityType<MagicMirrorPartBlockEntity>> MAGIC_MIRROR_PART =
            BLOCK_ENTITIES.register("magic_mirror_part", () -> BlockEntityType.Builder.of(MagicMirrorPartBlockEntity::new, Blocks.MAGIC_MIRROR_PART.get()).build(null));

    private BlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
