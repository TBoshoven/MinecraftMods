package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.SmithingRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import static net.minecraft.item.Items.ENDER_PEARL;

class Recipes extends RecipeProvider {
    Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        Items.DOORKNOBS.values().stream().forEach(
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
                                .unlockedBy("ender_pearls", InventoryChangeTrigger.Instance.hasItems(ENDER_PEARL))
                                .save(consumer);
                    }
                    Item netheriteSmithingBase = doorknob.getNetheriteSmithingBase();
                    if (netheriteSmithingBase != null) {
                        ResourceLocation doorknobLocation = ForgeRegistries.ITEMS.getKey(doorknob);
                        if (doorknobLocation != null) {
                            SmithingRecipeBuilder.smithing(
                                            Ingredient.of(netheriteSmithingBase),
                                            Ingredient.of(net.minecraft.item.Items.NETHERITE_INGOT),
                                            doorknob
                                    )
                                    .unlocks("has_netherite_ingot", has(net.minecraft.item.Items.NETHERITE_INGOT))
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
