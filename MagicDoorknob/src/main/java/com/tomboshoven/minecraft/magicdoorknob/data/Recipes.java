package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

import static net.minecraft.item.Items.ENDER_PEARL;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        Items.DOORKNOBS.values().forEach(
                doorknob -> ShapedRecipeBuilder.shapedRecipe(doorknob)
                        .patternLine(" # ")
                        .patternLine("#@#")
                        .patternLine(" # ")
                        .key('#', doorknob.getRecipeTag())
                        .key('@', Tags.Items.ENDER_PEARLS)
                        .setGroup("magic_mirror")
                        .addCriterion("ender_pearls", InventoryChangeTrigger.Instance.forItems(ENDER_PEARL))
                        .build(consumer)
        );
    }
}
