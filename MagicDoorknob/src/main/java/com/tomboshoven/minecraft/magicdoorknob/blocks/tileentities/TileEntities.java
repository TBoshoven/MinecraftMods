package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("ConstantConditions")
public final class TileEntities {
    static final TileEntityType<MagicDoorTileEntity> MAGIC_DOOR = TileEntityType.Builder.create(MagicDoorTileEntity::new, Blocks.MAGIC_DOOR).build(null);
    static final TileEntityType<MagicDoorwayTileEntity> MAGIC_DOORWAY = TileEntityType.Builder.create(MagicDoorwayTileEntity::new, Blocks.MAGIC_DOORWAY).build(null);

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt) {
        IForgeRegistry<TileEntityType<?>> registry = evt.getRegistry();

        MAGIC_DOOR.setRegistryName(MagicDoorknobMod.MOD_ID, "magic_door");
        registry.register(MAGIC_DOOR);
        MAGIC_DOORWAY.setRegistryName(MagicDoorknobMod.MOD_ID, "magic_doorway");
        registry.register(MAGIC_DOORWAY);
    }
}
