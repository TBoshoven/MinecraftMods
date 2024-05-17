package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Collection of all blocks in the mod.
 */
public final class Blocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagicMirrorMod.MOD_ID);

    /**
     * A magic mirror block. Shows you your reflection, and more!
     */
    public static final RegistryObject<Block> MAGIC_MIRROR =
            BLOCKS.register("magic_mirror",
                    () -> new MagicMirrorBlock(
                        Block.Properties.of(new Material.Builder(MaterialColor.COLOR_GRAY).build()).strength(.8f).sound(SoundType.GLASS)
                    )
            );

    private Blocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
