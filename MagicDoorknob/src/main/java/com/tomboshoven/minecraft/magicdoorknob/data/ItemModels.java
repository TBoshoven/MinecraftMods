package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class ItemModels extends ItemModelProvider {
    ItemModels(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, MagicDoorknobMod.MOD_ID, helper);
    }

    @Override
    protected void registerModels() {
        Items.DOORKNOBS.values().forEach(
                doorknob -> {
                    ResourceLocation registryName = doorknob.getRegistryName();
                    if (registryName != null) {
                        getBuilder(registryName.getPath())
                                .parent(new ModelFile.ExistingModelFile(modLoc("item/magic_doorknob"), existingFileHelper))
                                .texture("main", doorknob.getMainMaterial().texture());
                    }
                }
        );
    }

    @Override
    public String getName() {
        return String.format("%s Item Models", MagicDoorknobMod.MOD_ID);
    }
}
