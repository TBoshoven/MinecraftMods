package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

import static net.minecraft.world.item.Items.ENDER_PEARL;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class Recipes extends RecipeProvider {
    Recipes(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        Items.DOORKNOBS.values().forEach(
                doorknob -> ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, doorknob.get())
                        .pattern(" # ")
                        .pattern("#@#")
                        .pattern(" # ")
                        .define('#', doorknob.get().getIngredient())
                        .define('@', Tags.Items.ENDER_PEARLS)
                        .group("magic_doorknob")
                        .unlockedBy("ender_pearls", InventoryChangeTrigger.TriggerInstance.hasItems(ENDER_PEARL))
                        .save(consumer)
        );
    }
}
