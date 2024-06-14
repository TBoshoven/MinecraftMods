package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ItemBasedMagicMirrorBlockEntityModifier extends MagicMirrorBlockEntityModifier {
    final ItemStack item;

    /**
     * @param item The item that went into the mirror to create this modifier.
     */
    public ItemBasedMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier);
        this.item = item;
    }

    public ItemBasedMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        super(modifier);
        CompoundTag itemCompound = nbt.getCompound("Item");
        item = ItemStack.parse(lookupProvider, itemCompound).orElse(ItemStack.EMPTY);
    }

    @Override
    public CompoundTag write(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        super.write(nbt, lookupProvider);
        Tag itemTag = item.save(lookupProvider, new CompoundTag());
        nbt.put("Item", itemTag);
        return nbt;
    }

    @Override
    public void remove(Level world, BlockPos pos) {
        Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), this.item);
    }
}
