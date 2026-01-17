package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;
import java.util.Optional;

/**
 * Tile entity for the magic door parts.
 */
public class MagicDoorBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    // The doorknob held by the door.
    // Only one door block in the doorway can hold this.
    // The Optional is used for backwards compatibility: if no data is present, the top part of a door is assumed to
    // hold a simple version of its doorknob.
    Optional<ItemStack> doorknob = Optional.of(ItemStack.EMPTY);

    public MagicDoorBlockEntity() {
        super(Objects.requireNonNull(BlockEntities.MAGIC_DOOR.get()));
    }

    @Override
    protected CompoundNBT saveInternal(CompoundNBT compound) {
        CompoundNBT t = super.saveInternal(compound);
        doorknob.ifPresent(doorknob -> {
            if (!doorknob.isEmpty()) {
                CompoundNBT itemTag = doorknob.save(new CompoundNBT());
                t.put("doorknob", itemTag);
            }
        });
        return t;
    }

    @Override
    public void load(CompoundNBT compound) {
        super.load(compound);
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
