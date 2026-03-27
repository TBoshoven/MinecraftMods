package com.tomboshoven.minecraft.magicdoorknob.client.blockcolorhandlers;

import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.client.color.block.BlockTintSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Class for registering tint sources in the clients.
 */
public final class BlockTintSources {
    // The number of tint sources supported for passthrough
    private static final int MAPPED_BLOCK_TINT_SOURCES = 4;

    private BlockTintSources() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(BlockTintSources::registerBlockTintSources);
    }

    /**
     * Register all block tint sources.
     */
    private static void registerBlockTintSources(RegisterColorHandlersEvent.BlockTintSources event) {
        List<BlockTintSource> blockTintSources = IntStream.range(0, MAPPED_BLOCK_TINT_SOURCES)
                .mapToObj(DoorwayBlockTintSource::new)
                .map(BlockTintSource.class::cast)
                .toList();
        event.register(blockTintSources, Blocks.MAGIC_DOORWAY.get(), Blocks.MAGIC_DOOR.get());
    }
}
