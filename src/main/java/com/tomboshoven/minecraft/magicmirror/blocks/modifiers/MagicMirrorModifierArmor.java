package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic mirror modifier that allows it to be used as an armor stand for switching an entire set of armor.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorModifierArmor extends MagicMirrorModifier {
    @Override
    public String getName() {
        return "armor";
    }

    @Override
    public boolean canModify(World worldIn, BlockPos pos, ItemStack heldItem) {
        // Must be activated using an armor stand.
        if (heldItem.getItem() != Items.ARMOR_STAND) {
            return false;
        }

        // Must not have an armor modifier yet.
        return !hasModifierOfType(worldIn, pos);
    }

    @Override
    MagicMirrorTileEntityModifier createTileEntityModifier() {
        return new MagicMirrorTileEntityModifierArmor(this);
    }
}
