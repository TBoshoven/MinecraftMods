package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all block entities in the mod.
 */
@SuppressWarnings("ConstantConditions")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class TileEntities {
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MagicMirrorMod.MOD_ID);

    /**
     * Core that has all the actual logic.
     */
    public static final RegistryObject<BlockEntityType<MagicMirrorCoreTileEntity>> MAGIC_MIRROR_CORE =
            TILE_ENTITIES.register("magic_mirror_core", () -> BlockEntityType.Builder.of(MagicMirrorCoreTileEntity::new, Blocks.MAGIC_MIRROR_CORE.get()).build(null));
    /**
     * Parts that defer logic to the core.
     */
    public static final RegistryObject<BlockEntityType<MagicMirrorPartTileEntity>> MAGIC_MIRROR_PART =
            TILE_ENTITIES.register("magic_mirror_part", () -> BlockEntityType.Builder.of(MagicMirrorPartTileEntity::new, Blocks.MAGIC_MIRROR_PART.get()).build(null));

    private TileEntities() {
    }

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
