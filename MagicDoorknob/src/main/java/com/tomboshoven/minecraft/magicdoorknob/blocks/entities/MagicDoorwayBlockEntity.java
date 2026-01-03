package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

/**
 * Tile entity for the magic doorway parts.
 */
public class MagicDoorwayBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    // Whether the door is currently closing
    // Used for preventing suffocation because someone closed the door
    private boolean isClosing = false;

    public MagicDoorwayBlockEntity() {
        super(BlockEntities.MAGIC_DOORWAY.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        World level = getLevel();
        // Schedule the block tick in case we're closing
        if (isClosing && level != null) {
            level.getBlockTicks().scheduleTick(getBlockPos(), getBlockState().getBlock(), 10);
        }
    }

    @Override
    protected CompoundNBT saveInternal(CompoundNBT compound) {
        CompoundNBT result = super.saveInternal(compound);
        result.putBoolean("isClosing", isClosing);
        return result;
    }

    @Override
    protected void loadInternal(CompoundNBT compound) {
        super.loadInternal(compound);
        isClosing = compound.getBoolean("isClosing");
    }

    /**
     * @return Whether the doorway is currently closing.
     */
    public boolean isClosing() {
        return this.isClosing;
    }

    /**
     * Mark the doorway as closing, causing it to close when there are no more living entities in it.
     */
    public void setClosing() {
        this.isClosing = true;
    }
}
