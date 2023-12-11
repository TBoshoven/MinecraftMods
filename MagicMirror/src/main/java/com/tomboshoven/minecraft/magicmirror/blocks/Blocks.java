package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Collection of all blocks in the mod.
 */
public final class Blocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MagicMirrorMod.MOD_ID);

    private static final BlockBehaviour.Properties MIRROR_PROPERTIES = Block.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.8f).sound(SoundType.GLASS);

    /**
     * The core that has all the actual logic.
     */
    public static final Supplier<Block> MAGIC_MIRROR_CORE =
            BLOCKS.registerBlock("magic_mirror_core", MagicMirrorCoreBlock::new, MIRROR_PROPERTIES);
    /**
     * A part that refers all the logic to the core.
     */
    public static final Supplier<Block> MAGIC_MIRROR_PART =
            BLOCKS.registerBlock("magic_mirror_part", MagicMirrorPartBlock::new, MIRROR_PROPERTIES);
    /**
     * An inactive mirror part.
     */
    public static final Supplier<Block> MAGIC_MIRROR_INACTIVE =
            BLOCKS.registerBlock("magic_mirror_inactive", MagicMirrorInactiveBlock::new, MIRROR_PROPERTIES);

    private Blocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
