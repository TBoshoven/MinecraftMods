package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the magic doorway parts.
 */
public class MagicDoorwayBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    // Whether the door is currently closing
    // Used for preventing suffocation because someone closed the door
    private boolean isClosing = false;

    public MagicDoorwayBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_DOORWAY.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Level level = getLevel();
        // Schedule the block tick in case we're closing
        if (isClosing && level != null) {
            level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 10);
        }
    }

    @Override
    protected void saveInternal(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.saveInternal(compound, lookupProvider);
        compound.putBoolean("isClosing", isClosing);
    }

    @Override
    protected void loadInternal(CompoundTag compound, HolderLookup.Provider lookupProvider) {
        super.loadInternal(compound, lookupProvider);
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
