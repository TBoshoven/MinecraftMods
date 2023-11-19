package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class ItemModels extends ItemModelProvider {
    ItemModels(PackOutput output, ExistingFileHelper helper) {
        super(output, MagicMirrorMod.MOD_ID, helper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation magicMirrorId = Items.MAGIC_MIRROR.getId();
        getBuilder(magicMirrorId.getPath())
                .parent(new ModelFile.ExistingModelFile(modLoc("block/magic_mirror"), existingFileHelper));
    }

    @Override
    public @NotNull String getName() {
        return String.format("%s Item Models", MagicMirrorMod.MOD_ID);
    }
}
