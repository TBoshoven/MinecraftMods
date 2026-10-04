package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Supplier;

import static net.minecraft.world.item.Items.ENDER_PEARL;

final class Recipes extends RecipeProvider {
    public Recipes(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    protected void buildRecipes() {
        Items.DOORKNOBS.values().stream().map(Supplier::get).forEach(
                doorknob -> {
                    TagKey<Item> craftingIngredients = doorknob.getCraftingIngredients();
                    if (craftingIngredients != null) {
                        shaped(RecipeCategory.TOOLS, doorknob)
                                .pattern(" # ")
                                .pattern("#@#")
                                .pattern(" # ")
                                .define('#', craftingIngredients)
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
                                        tag(net.minecraft.tags.ItemTags.NETHERITE_TOOL_MATERIALS),
                                        RecipeCategory.TOOLS,
                                        doorknob
                                )
                                .unlocks("has_netherite_ingot", has(ItemTags.NETHERITE_TOOL_MATERIALS))
                                .save(this.output, ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(doorknob).withSuffix("_smithing")));
                    }
                }
        );
    }
}
