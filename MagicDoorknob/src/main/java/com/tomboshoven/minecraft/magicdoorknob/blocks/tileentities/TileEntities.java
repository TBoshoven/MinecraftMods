package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public final class TileEntities {
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MagicDoorknobMod.MOD_ID);

    static final RegistryObject<TileEntityType<MagicDoorTileEntity>> MAGIC_DOOR =
            TILE_ENTITIES.register("magic_door",
                    () -> TileEntityType.Builder.of(MagicDoorTileEntity::new, Blocks.MAGIC_DOOR.get()).build(null)
            );
    static final RegistryObject<TileEntityType<MagicDoorwayTileEntity>> MAGIC_DOORWAY =
            TILE_ENTITIES.register("magic_doorway",
                    () -> TileEntityType.Builder.of(MagicDoorwayTileEntity::new, Blocks.MAGIC_DOORWAY.get()).build(null)
            );

    private TileEntities() {
    }

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
