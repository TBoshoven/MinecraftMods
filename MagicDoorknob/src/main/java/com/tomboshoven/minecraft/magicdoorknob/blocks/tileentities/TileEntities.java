package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import com.tomboshoven.minecraft.magicdoorknob.ModMagicDoorknob;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class TileEntities {
    static TileEntityType<TileEntityMagicDoor> MAGIC_DOOR = TileEntityType.Builder.create(TileEntityMagicDoor::new, Blocks.blockMagicDoor).build(null);
    static TileEntityType<TileEntityMagicDoorway> MAGIC_DOORWAY = TileEntityType.Builder.create(TileEntityMagicDoorway::new, Blocks.blockMagicDoorway).build(null);

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt) {
        IForgeRegistry<TileEntityType<?>> registry = evt.getRegistry();

        MAGIC_DOOR.setRegistryName(ModMagicDoorknob.MOD_ID, "magic_door");
        registry.register(MAGIC_DOOR);
        MAGIC_DOORWAY.setRegistryName(ModMagicDoorknob.MOD_ID, "magic_doorway");
        registry.register(MAGIC_DOORWAY);
    }
}
