package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public final class BlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MagicDoorknobMod.MOD_ID);

    static final Supplier<BlockEntityType<MagicDoorBlockEntity>> MAGIC_DOOR =
            BLOCK_ENTITIES.register("magic_door",
                    () -> BlockEntityType.Builder.of(MagicDoorBlockEntity::new, Blocks.MAGIC_DOOR.get()).build(null)
            );
    static final Supplier<BlockEntityType<MagicDoorwayBlockEntity>> MAGIC_DOORWAY =
            BLOCK_ENTITIES.register("magic_doorway",
                    () -> BlockEntityType.Builder.of(MagicDoorwayBlockEntity::new, Blocks.MAGIC_DOORWAY.get()).build(null)
            );

    private BlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
