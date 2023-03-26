package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.items.Items;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

import static net.minecraft.world.item.Items.ENDER_EYE;
import static net.minecraftforge.common.Tags.Items.GLASS_PANES;
import static net.minecraftforge.common.Tags.Items.RODS_WOODEN;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class Recipes extends RecipeProvider {
    Recipes(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.MAGIC_MIRROR.get(), 2)
                .pattern("|e|")
                .pattern("| |")
                .pattern("|#|")
                .define('|', RODS_WOODEN)
                .define('e', ENDER_EYE)
                .define('#', GLASS_PANES)
                .group("magic_mirror")
                .unlockedBy("ender_eye", InventoryChangeTrigger.TriggerInstance.hasItems(ENDER_EYE))
                .save(consumer);
    }
}
