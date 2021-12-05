package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("ConstantConditions")
public final class BlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MagicDoorknobMod.MOD_ID);

    static final RegistryObject<BlockEntityType<MagicDoorBlockEntity>> MAGIC_DOOR =
            BLOCK_ENTITIES.register("magic_door",
                    () -> BlockEntityType.Builder.of(MagicDoorBlockEntity::new, Blocks.MAGIC_DOOR.get()).build(null)
            );
    static final RegistryObject<BlockEntityType<MagicDoorwayBlockEntity>> MAGIC_DOORWAY =
            BLOCK_ENTITIES.register("magic_doorway",
                    () -> BlockEntityType.Builder.of(MagicDoorwayBlockEntity::new, Blocks.MAGIC_DOORWAY.get()).build(null)
            );

    private BlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
