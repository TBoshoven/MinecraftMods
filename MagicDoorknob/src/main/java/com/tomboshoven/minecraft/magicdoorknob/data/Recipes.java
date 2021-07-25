package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

import static net.minecraft.item.Items.ENDER_PEARL;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class Recipes extends RecipeProvider {
    Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        Items.DOORKNOBS.values().forEach(
                doorknob -> ShapedRecipeBuilder.shaped(doorknob)
                        .pattern(" # ")
                        .pattern("#@#")
                        .pattern(" # ")
                        .define('#', doorknob.getIngredient())
                        .define('@', Tags.Items.ENDER_PEARLS)
                        .group("magic_doorknob")
                        .unlocks("ender_pearls", InventoryChangeTrigger.Instance.hasItem(ENDER_PEARL))
                        .save(consumer)
        );
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicDoorknobMod.MOD_ID, super.getName());
    }
}
