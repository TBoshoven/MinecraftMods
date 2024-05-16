package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemBasedMagicMirrorTileEntityModifier extends MagicMirrorTileEntityModifier {
    final ItemStack item;

    /**
     * @param item The item that went into the mirror to create this modifier.
     */
    public ItemBasedMagicMirrorTileEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier);
        this.item = item;
    }

    public ItemBasedMagicMirrorTileEntityModifier(MagicMirrorModifier modifier, CompoundNBT nbt) {
        super(modifier);
        CompoundNBT itemCompound = nbt.getCompound("Item");
        if (itemCompound.isEmpty()) {
            item = getItemStackOldNbt(nbt);
        }
        else {
            item = ItemStack.of(itemCompound);
        }
    }

    /**
     * Reconstruct the item stack from old NBT compounds, which kept track of things separately.
     * This should be safe to remove over time.
     *
     * @param nbt The NBT compound to attempt to reconstruct the item stack for.
     * @return the reconstructed item stack.
     */
    protected ItemStack getItemStackOldNbt(CompoundNBT nbt) {
        return ItemStack.EMPTY;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        CompoundNBT itemCompound = item.serializeNBT();
        nbt.put("Item", itemCompound);
        return nbt;
    }

    @Override
    public void remove(World world, BlockPos pos) {
        InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), this.item);
    }
}
