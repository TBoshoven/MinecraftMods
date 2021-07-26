package com.tomboshoven.minecraft.magicdoorknob.blocks;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of all blocks in the mod.
 */
@EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Blocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagicDoorknobMod.MOD_ID);

    /**
     * The material the magic door and doorway are made of:
     * - Not liquid
     * - Solid
     * - Block movement
     * - Opaque
     * - Not flammable
     * - Not replaceable
     * - Block pushes
     */
    private static final Material DOORWAY_MATERIAL = new Material(MaterialColor.NONE, false, true, true, true, false, false, PushReaction.BLOCK);

    /**
     * A magic doorway block. Generated by a magic doorknob.
     */
    public static final RegistryObject<Block> MAGIC_DOORWAY =
            BLOCKS.register("magic_doorway",  () -> new MagicDoorwayBlock(Block.Properties.of(DOORWAY_MATERIAL).noOcclusion()));

    /**
     * A magic door block. Generated by a magic doorknob and the entrance to a magic doorway.
     */
    public static final RegistryObject<Block> MAGIC_DOOR =
            BLOCKS.register("magic_door", () -> new MagicDoorBlock(Block.Properties.of(DOORWAY_MATERIAL).noOcclusion()));

    private Blocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
