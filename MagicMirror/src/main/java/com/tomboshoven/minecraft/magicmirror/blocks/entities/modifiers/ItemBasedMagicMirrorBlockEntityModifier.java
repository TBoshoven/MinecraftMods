package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

    ItemBasedMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ValueInput input) {
        super(modifier);
        item = input.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public void save(ValueOutput output) {
        super.save(output);
        output.store("item", ItemStack.CODEC, item);
    }

    @Override
    public void remove(@Nullable Level world, BlockPos pos) {
        if (world != null) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), item);
        }
    }
}
