package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.nbt.CompoundNBT;

/**
 * Tile entity for the magic door parts.
 */
public class MagicDoorBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    // Whether the door is a "primary" one, meaning it holds the doorknob item.
    // Used in double doorways, to prevent duplication of the doorknob.
    // Note that this is only ever set for the tops of the doors.
    boolean isPrimary = true;

    public MagicDoorBlockEntity() {
        super(BlockEntities.MAGIC_DOOR.get());
    }

    @Override
    protected CompoundNBT saveInternal(CompoundNBT compound) {
        compound = super.saveInternal(compound);
        compound.putBoolean("isPrimary", isPrimary);
        return compound;
    }

    @Override
    protected void loadInternal(CompoundNBT compound) {
        super.loadInternal(compound);
        if (compound.contains("isPrimary")) {
            isPrimary = compound.getBoolean("isPrimary");
        }
    }

    /**
     * @return Whether this door part is "primary", meaning it holds the doorknob.
     */
    public boolean isPrimary() {
        return isPrimary;
    }

    /**
     * @param isPrimary Whether this door part is "primary", meaning it holds the doorknob.
     */
    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
