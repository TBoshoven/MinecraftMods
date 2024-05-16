package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public final class BlockEntities {
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MagicMirrorMod.MOD_ID);

    public static final RegistryObject<TileEntityType<MagicMirrorCoreBlockEntity>> MAGIC_MIRROR_CORE =
            TILE_ENTITIES.register("magic_mirror_core", () -> TileEntityType.Builder.of(MagicMirrorCoreBlockEntity::new, Blocks.MAGIC_MIRROR.get()).build(null));
    public static final RegistryObject<TileEntityType<MagicMirrorPartBlockEntity>> MAGIC_MIRROR_PART =
            TILE_ENTITIES.register("magic_mirror_part", () -> TileEntityType.Builder.of(MagicMirrorPartBlockEntity::new, Blocks.MAGIC_MIRROR.get()).build(null));

    private BlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
