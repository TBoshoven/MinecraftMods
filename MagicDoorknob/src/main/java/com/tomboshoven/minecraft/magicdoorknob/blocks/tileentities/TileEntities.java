package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public final class TileEntities {
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MagicDoorknobMod.MOD_ID);

    static final RegistryObject<BlockEntityType<MagicDoorTileEntity>> MAGIC_DOOR =
            TILE_ENTITIES.register("magic_door",
                    () -> BlockEntityType.Builder.of(MagicDoorTileEntity::new, Blocks.MAGIC_DOOR.get()).build(null)
            );
    static final RegistryObject<BlockEntityType<MagicDoorwayTileEntity>> MAGIC_DOORWAY =
            TILE_ENTITIES.register("magic_doorway",
                    () -> BlockEntityType.Builder.of(MagicDoorwayTileEntity::new, Blocks.MAGIC_DOORWAY.get()).build(null)
            );

    private TileEntities() {
    }

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
