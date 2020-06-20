package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

import static net.minecraft.item.Items.ENDER_EYE;
import static net.minecraftforge.common.Tags.Items.GLASS_PANES;
import static net.minecraftforge.common.Tags.Items.RODS_WOODEN;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class Recipes extends RecipeProvider {
    Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(Items.MAGIC_MIRROR, 2)
                .patternLine("|e|")
                .patternLine("| |")
                .patternLine("|#|")
                .key('|', RODS_WOODEN)
                .key('e', ENDER_EYE)
                .key('#', GLASS_PANES)
                .setGroup("magic_mirror")
                .addCriterion("ender_eye", InventoryChangeTrigger.Instance.forItems(ENDER_EYE))
                .build(consumer);
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicMirrorMod.MOD_ID, super.getName());
    }
}
