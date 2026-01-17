package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Block entity for the magic door parts.
 */
public class MagicDoorBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    // The doorknob held by the door.
    // Only one door block in the doorway can hold this.
    // The Optional is used for backwards compatibility: if no data is present, the top part of a door is assumed to
    // hold a simple version of its doorknob.
    Optional<ItemStack> doorknob = Optional.of(ItemStack.EMPTY);

    public MagicDoorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_DOOR.get(), pos, state);
    }

    @Override
    protected CompoundTag saveInternal(CompoundTag compound) {
        CompoundTag t = super.saveInternal(compound);
        doorknob.ifPresent(doorknob -> {
            if (!doorknob.isEmpty()) {
                Tag itemTag = doorknob.save(new CompoundTag());
                t.put("doorknob", itemTag);
            }
        });
        return t;
    }

    @Override
    protected void loadInternal(CompoundTag compound) {
        super.loadInternal(compound);
        if (compound.contains("doorknob")) {
            doorknob = Optional.of(ItemStack.of(compound.getCompound("doorknob")));
        }
        else {
            doorknob = Optional.empty();
        }
    }

    /**
     * @return The doorknob held by this door. Optional for backward compatibility.
     */
    public Optional<ItemStack> getDoorknobItem() {
        return doorknob;
    }

    /**
     * @param doorknob The doorknob held by this door. Optional for backward compatibility.
     */
    public void setDoorknobItem(ItemStack doorknob) {
        this.doorknob = Optional.of(doorknob);
    }
}
