package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("ConstantConditions")
public final class TileEntities {
    static final TileEntityType<MagicMirrorCoreTileEntity> MAGIC_MIRROR_CORE = TileEntityType.Builder.create(MagicMirrorCoreTileEntity::new, Blocks.MAGIC_MIRROR).build(null);
    static final TileEntityType<MagicMirrorPartTileEntity> MAGIC_MIRROR_PART = TileEntityType.Builder.create(MagicMirrorPartTileEntity::new, Blocks.MAGIC_MIRROR).build(null);

    @SubscribeEvent
    public static void registerTileEntities(Register<TileEntityType<?>> evt) {
        IForgeRegistry<TileEntityType<?>> registry = evt.getRegistry();

        MAGIC_MIRROR_CORE.setRegistryName(MagicMirrorMod.MOD_ID, "magic_mirror_core");
        registry.register(MAGIC_MIRROR_CORE);
        MAGIC_MIRROR_PART.setRegistryName(MagicMirrorMod.MOD_ID, "magic_mirror_part");
        registry.register(MAGIC_MIRROR_PART);
    }
}
