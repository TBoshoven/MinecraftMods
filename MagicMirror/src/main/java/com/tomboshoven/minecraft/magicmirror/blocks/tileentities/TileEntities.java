package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class TileEntities {
    static TileEntityType<TileEntityMagicMirrorCore> MAGIC_MIRROR_CORE = TileEntityType.Builder.create(TileEntityMagicMirrorCore::new, Blocks.blockMagicMirror).build(null);
    static TileEntityType<TileEntityMagicMirrorPart> MAGIC_MIRROR_PART = TileEntityType.Builder.create(TileEntityMagicMirrorPart::new, Blocks.blockMagicMirror).build(null);

    @SubscribeEvent
    public static void registerTileEntities(Register<TileEntityType<?>> evt) {
        IForgeRegistry<TileEntityType<?>> registry = evt.getRegistry();

        MAGIC_MIRROR_CORE.setRegistryName(ModMagicMirror.MOD_ID, "magic_mirror_core");
        registry.register(MAGIC_MIRROR_CORE);
        MAGIC_MIRROR_PART.setRegistryName(ModMagicMirror.MOD_ID, "magic_mirror_part");
        registry.register(MAGIC_MIRROR_PART);
    }
}
