package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all blocks in the mod.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Blocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagicMirrorMod.MOD_ID);

    private static final BlockBehaviour.Properties MIRROR_PROPERTIES = Block.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.8f).sound(SoundType.GLASS);

    /**
     * The core that has all the actual logic.
     */
    public static final RegistryObject<Block> MAGIC_MIRROR_CORE =
            BLOCKS.register("magic_mirror_core", () -> new MagicMirrorCoreBlock(MIRROR_PROPERTIES));
    /**
     * A part that refers all the logic to the core.
     */
    public static final RegistryObject<Block> MAGIC_MIRROR_PART =
            BLOCKS.register("magic_mirror_part", () -> new MagicMirrorPartBlock(MIRROR_PROPERTIES));
    /**
     * An inactive mirror part.
     */
    public static final RegistryObject<Block> MAGIC_MIRROR_INACTIVE =
            BLOCKS.register("magic_mirror_inactive", () -> new MagicMirrorInactiveBlock(MIRROR_PROPERTIES));

    private Blocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
