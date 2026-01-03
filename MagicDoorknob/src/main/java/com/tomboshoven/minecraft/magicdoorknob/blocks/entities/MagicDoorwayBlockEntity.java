package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            // When this block is destroyed (manually or by closing the door), replace it by its base block.
            level.setBlockAndUpdate(pos, getBaseBlockState());
        }
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
    protected void saveInternal(ValueOutput output) {
        super.saveInternal(output);
        output.putBoolean("isClosing", isClosing);
    }

    @Override
    protected void loadInternal(ValueInput valueInput) {
        super.loadInternal(valueInput);
        isClosing = valueInput.getBooleanOr("isClosing", false);
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
