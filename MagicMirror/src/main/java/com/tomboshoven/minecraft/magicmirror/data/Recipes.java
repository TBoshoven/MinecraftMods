package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import static net.minecraft.item.Items.ENDER_EYE;
import static net.minecraftforge.common.Tags.Items.GLASS_PANES;
import static net.minecraftforge.common.Tags.Items.RODS_WOODEN;

class Recipes extends RecipeProvider {
    Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Items.MAGIC_MIRROR.get(), 2)
                .pattern("|e|")
                .pattern("| |")
                .pattern("|#|")
                .define('|', RODS_WOODEN)
                .define('e', ENDER_EYE)
                .define('#', GLASS_PANES)
                .group("magic_mirror")
                .unlocks("ender_eye", InventoryChangeTrigger.Instance.hasItem(ENDER_EYE))
                .save(consumer);
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicMirrorMod.MOD_ID, super.getName());
    }
}
