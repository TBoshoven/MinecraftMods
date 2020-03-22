package com.tomboshoven.minecraft.magicmirror.blocks;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all blocks in the mod.
 */
@EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Blocks {
    /**
     * A magic mirror block. Shows you your reflection, and more!
     */
    public static final Block MAGIC_MIRROR = new MagicMirrorBlock(
            Block.Properties.create(
                    new Material.Builder(MaterialColor.GRAY).build())
                    .hardnessAndResistance(.8f)
                    .sound(SoundType.GLASS)
    ).setRegistryName(MagicMirrorMod.MOD_ID, "magic_mirror");

    private Blocks() {
    }

    @SuppressWarnings("BoundedWildcard")
    @SubscribeEvent
    public static void registerBlocks(Register<Block> event) {
        event.getRegistry().registerAll(MAGIC_MIRROR);
    }
}
