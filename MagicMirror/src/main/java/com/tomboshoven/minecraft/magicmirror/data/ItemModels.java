package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, MagicMirrorMod.MOD_ID, helper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation registryName = Items.MAGIC_MIRROR.getRegistryName();
        if (registryName != null) {
            getBuilder(registryName.getPath())
                    .parent(new ModelFile.ExistingModelFile(modLoc("block/magic_mirror"), existingFileHelper));
        }
    }

    @Override
    public String getName() {
        return String.format("%s Item Models", MagicMirrorMod.MOD_ID);
    }
}
