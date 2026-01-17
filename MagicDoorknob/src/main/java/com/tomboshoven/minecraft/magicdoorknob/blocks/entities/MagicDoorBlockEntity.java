package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock.PART;

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
    protected void saveInternal(ValueOutput output) {
        super.saveInternal(output);
        output.putBoolean("isPrimary", isPrimary);
    }

    @Override
    protected void loadInternal(ValueInput valueInput) {
        super.loadInternal(valueInput);
        isPrimary = valueInput.getBooleanOr("isPrimary", true);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (isPrimary && state.getValue(PART) == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
            // Spawn the doorknob
            Item doorknob = getDoorknob();
            if (doorknob != null && level != null) {
                Containers.dropItemStack(level, pos.getX(), pos.getY() - .5f, pos.getZ(), new ItemStack(doorknob, 1));
            }
        }
        super.preRemoveSideEffects(pos, state);
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
