package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class ItemBasedMagicMirrorBlockEntityModifier extends MagicMirrorBlockEntityModifier {
    final ItemStack item;

    /**
     * @param item The item that went into the mirror to create this modifier.
     */
    ItemBasedMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier);
        this.item = item;
    }

    ItemBasedMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        super(modifier);
        item = nbt.getCompound("Item")
                .flatMap(itemCompound -> ItemStack.parse(lookupProvider, itemCompound))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public CompoundTag write(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        super.write(nbt, lookupProvider);
        Tag itemTag = item.save(lookupProvider, new CompoundTag());
        nbt.put("Item", itemTag);
        return nbt;
    }

    @Override
    public void remove(@Nullable Level world, BlockPos pos) {
        if (world != null) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), item);
        }
    }
}
