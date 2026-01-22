package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.item.Items.ENDER_PEARL;

class Recipes extends RecipeProvider {
    Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        Items.DOORKNOBS.values().stream().map(Supplier::get).forEach(
                doorknob -> {
                    Ingredient craftingIngredient = doorknob.getCraftingIngredient();
                    if (craftingIngredient != null) {
                        ShapedRecipeBuilder.shaped(doorknob)
                                .pattern(" # ")
                                .pattern("#@#")
                                .pattern(" # ")
                                .define('#', craftingIngredient)
                                .define('@', Tags.Items.ENDER_PEARLS)
                                .group("magic_doorknob")
                                .unlockedBy("ender_pearls", InventoryChangeTrigger.TriggerInstance.hasItems(ENDER_PEARL))
                                .save(consumer);
                    }
                    Item netheriteSmithingBase = doorknob.getNetheriteSmithingBase();
                    if (netheriteSmithingBase != null) {
                        ResourceLocation doorknobLocation = ForgeRegistries.ITEMS.getKey(doorknob);
                        if (doorknobLocation != null) {
                            UpgradeRecipeBuilder.smithing(
                                            Ingredient.of(netheriteSmithingBase),
                                            Ingredient.of(net.minecraft.world.item.Items.NETHERITE_INGOT),
                                            doorknob
                                    )
                                    .unlocks("has_netherite_ingot", has(net.minecraft.world.item.Items.NETHERITE_INGOT))
                                    .save(consumer, new ResourceLocation(doorknobLocation.getNamespace(), doorknobLocation.getPath() + "_smithing"));
                        }
                    }
                }
        );
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicDoorknobMod.MOD_ID, super.getName());
    }
}
