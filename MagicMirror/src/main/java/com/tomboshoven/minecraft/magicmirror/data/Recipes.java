package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.items.Items;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.world.item.Items.ENDER_EYE;
import static net.neoforged.neoforge.common.Tags.Items.GLASS_PANES;
import static net.neoforged.neoforge.common.Tags.Items.RODS_WOODEN;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class Recipes extends RecipeProvider {
    Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.MAGIC_MIRROR.get(), 2)
                .pattern("|e|")
                .pattern("| |")
                .pattern("|#|")
                .define('|', RODS_WOODEN)
                .define('e', ENDER_EYE)
                .define('#', GLASS_PANES)
                .group("magic_mirror")
                .unlockedBy("ender_eye", InventoryChangeTrigger.TriggerInstance.hasItems(ENDER_EYE))
                .save(output);
    }
}
