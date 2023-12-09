package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

class ItemModels extends ItemModelProvider {
    ItemModels(PackOutput output, ExistingFileHelper helper) {
        super(output, MagicDoorknobMod.MOD_ID, helper);
    }

    @Override
    protected void registerModels() {
        Items.DOORKNOBS.values().forEach(
                doorknob -> {
                    ResourceLocation registryName = doorknob.getId();
                    getBuilder(registryName.getPath())
                            .parent(new ModelFile.ExistingModelFile(modLoc("item/magic_doorknob"), existingFileHelper))
                            .texture("main", doorknob.get().getMainMaterial().texture());
                }
        );
    }

    @Override
    public @NotNull String getName() {
        return String.format("%s Item Models", MagicDoorknobMod.MOD_ID);
    }
}
