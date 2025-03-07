package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.item.Items.ENDER_PEARL;

final class Recipes extends RecipeProvider {
    private Recipes(HolderLookup.Provider lookupProvider, RecipeOutput output) {
        super(lookupProvider, output);
    }

    @Override
    protected void buildRecipes() {
        Items.DOORKNOBS.values().forEach(
                doorknob -> shaped(RecipeCategory.TOOLS, doorknob.get())
                        .pattern(" # ")
                        .pattern("#@#")
                        .pattern(" # ")
                        .define('#', doorknob.get().getIngredients())
                        .define('@', Tags.Items.ENDER_PEARLS)
                        .group("magic_doorknob")
                        .unlockedBy("ender_pearls", InventoryChangeTrigger.TriggerInstance.hasItems(ENDER_PEARL))
                        .save(output)
        );
    }

    static class Runner extends RecipeProvider.Runner {
        Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> holderLookupProvider) {
            super(output, holderLookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider holderLookupProvider, RecipeOutput output) {
            return new Recipes(holderLookupProvider, output);
        }

        @Override
        public String getName() {
            return "Magic Doorknob Recipes";
        }
    }
}
