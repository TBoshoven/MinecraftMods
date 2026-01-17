package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the magic door parts.
 */
public class MagicDoorBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    // Whether the door is a "primary" one, meaning it holds the doorknob item.
    // Used in double doorways, to prevent duplication of the doorknob.
    // Note that this is only ever set for the tops of the doors.
    boolean isPrimary = true;

    public MagicDoorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_DOOR.get(), pos, state);
    }

    @Override
    protected void saveInternal(CompoundTag compound) {
        super.saveInternal(compound);
        compound.putBoolean("isPrimary", isPrimary);
    }

    @Override
    protected void loadInternal(CompoundTag compound) {
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
