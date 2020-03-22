package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.CreatureMagicMirrorTileEntityModifier;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic mirror modifier that changes the appearance of the reflection to be another creature's.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreatureMagicMirrorModifier extends MagicMirrorModifier {
    /**
     * Return whether the given item is a skull that can be used to apply this modifier.
     *
     * @param item The item to test.
     * @return Whether the item is a skull of the right type.
     */
    private static boolean isSupportedSkull(ItemStack item) {
        // Only support skeleton skulls for now
        return item.getItem() == Items.SKELETON_SKULL && Items.SKELETON_SKULL.getDamage(item) == 0;
    }

    @Override
    public String getName() {
        return "creature";
    }

    @Override
    public boolean canModify(World worldIn, BlockPos pos, ItemStack heldItem) {
        return isSupportedSkull(heldItem) && !hasModifierOfType(worldIn, pos);
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier() {
        return new CreatureMagicMirrorTileEntityModifier(this);
    }
}
