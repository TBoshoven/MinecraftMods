package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.minecraft.world.item.Items.ENDER_PEARL;

class Recipes extends RecipeProvider {
    Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        Items.DOORKNOBS.values().stream().map(Supplier::get).forEach(
                doorknob -> {
                    Ingredient craftingIngredient = doorknob.getCraftingIngredient();
                    if (craftingIngredient != null) {
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, doorknob)
                                .pattern(" # ")
                                .pattern("#@#")
                                .pattern(" # ")
                                .define('#', craftingIngredient)
                                .define('@', Tags.Items.ENDER_PEARLS)
                                .group("magic_doorknob")
                                .unlockedBy("ender_pearls", InventoryChangeTrigger.TriggerInstance.hasItems(ENDER_PEARL))
                                .save(output);
                    }
                    Item netheriteSmithingBase = doorknob.getNetheriteSmithingBase();
                    if (netheriteSmithingBase != null) {
                        SmithingTransformRecipeBuilder.smithing(
                                        Ingredient.of(net.minecraft.world.item.Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                        Ingredient.of(netheriteSmithingBase),
                                        Ingredient.of(net.minecraft.world.item.Items.NETHERITE_INGOT),
                                        RecipeCategory.TOOLS,
                                        doorknob
                                )
                                .unlocks("has_netherite_ingot", has(net.minecraft.world.item.Items.NETHERITE_INGOT))
                                .save(output, BuiltInRegistries.ITEM.getKey(doorknob).withSuffix("_smithing"));
                    }
                }
        );
    }
}
