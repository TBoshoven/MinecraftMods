package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A magic mirror modifier that allows it to be used as an armor stand for switching an entire set of armor.
 */
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
        TileEntityMagicMirrorBase magicMirrorTileEntity = getMagicMirrorTileEntity(worldIn, pos);
        if (magicMirrorTileEntity == null) {
            return false;
        }
        return magicMirrorTileEntity.getModifiers().stream()
                .noneMatch(magicMirrorModifier -> magicMirrorModifier instanceof MagicMirrorTileEntityModifierArmor);
    }

    @Override
    void apply(TileEntityMagicMirrorBase tileEntity, ItemStack heldItem) {
        tileEntity.addModifier(new MagicMirrorTileEntityModifierArmor(this));
        heldItem.shrink(1);
    }

    @Override
    public void apply(TileEntityMagicMirrorBase tileEntity, NBTTagCompound nbt) {
        MagicMirrorTileEntityModifierArmor magicMirrorTileEntityModifier = new MagicMirrorTileEntityModifierArmor(this);
        magicMirrorTileEntityModifier.readFromNBT(nbt);
        tileEntity.addModifier(magicMirrorTileEntityModifier);
    }
}
