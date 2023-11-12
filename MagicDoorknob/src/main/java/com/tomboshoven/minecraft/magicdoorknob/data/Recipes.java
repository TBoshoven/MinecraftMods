package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.item.Items.ENDER_PEARL;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class Recipes extends RecipeProvider {
    Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        Items.DOORKNOBS.values().forEach(
                doorknob -> ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, doorknob.get())
                        .pattern(" # ")
                        .pattern("#@#")
                        .pattern(" # ")
                        .define('#', doorknob.get().getIngredient())
                        .define('@', Tags.Items.ENDER_PEARLS)
                        .group("magic_doorknob")
                        .unlockedBy("ender_pearls", InventoryChangeTrigger.TriggerInstance.hasItems(ENDER_PEARL))
                        .save(output)
        );
    }
}
