package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class ItemModels extends ItemModelProvider {
    ItemModels(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, MagicMirrorMod.MOD_ID, helper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation magicMirrorId = Items.MAGIC_MIRROR.getId();
        if (magicMirrorId != null) {
            getBuilder(magicMirrorId.getPath())
                    .parent(new ModelFile.ExistingModelFile(modLoc("block/magic_mirror"), existingFileHelper));
        }
    }

    @Override
    public String getName() {
        return String.format("%s Item Models", MagicMirrorMod.MOD_ID);
    }
}
