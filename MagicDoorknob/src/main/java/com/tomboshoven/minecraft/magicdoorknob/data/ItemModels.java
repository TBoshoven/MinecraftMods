package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

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
                    ResourceLocation registryName = doorknob.getId();
                    if (registryName != null) {
                        getBuilder(registryName.getPath())
                                .parent(new ModelFile.ExistingModelFile(modLoc("item/magic_doorknob"), existingFileHelper))
                                .texture("main", doorknob.get().getMainMaterial().texture());
                    }
                }
        );
    }

    @Override
    public @NotNull String getName() {
        return String.format("%s Item Models", MagicDoorknobMod.MOD_ID);
    }
}
