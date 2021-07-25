package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public final class TileEntities {
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MagicMirrorMod.MOD_ID);

    static final RegistryObject<TileEntityType<MagicMirrorCoreTileEntity>> MAGIC_MIRROR_CORE =
            TILE_ENTITIES.register("magic_mirror_core", () -> TileEntityType.Builder.of(MagicMirrorCoreTileEntity::new, Blocks.MAGIC_MIRROR.get()).build(null));
    static final RegistryObject<TileEntityType<MagicMirrorPartTileEntity>> MAGIC_MIRROR_PART =
            TILE_ENTITIES.register("magic_mirror_part", () -> TileEntityType.Builder.of(MagicMirrorPartTileEntity::new, Blocks.MAGIC_MIRROR.get()).build(null));

    private TileEntities() {
    }

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
