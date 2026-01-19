package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
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
    // The length opf the doorway; used as a limit when closing the door
    int doorwayLength = 0;

    public MagicDoorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_DOOR.get(), pos, state);
    }

    @Override
    protected void saveInternal(CompoundTag compound) {
        super.saveInternal(compound);
        doorknob.ifPresent(doorknob -> {
            if (!doorknob.isEmpty()) {
                Tag itemTag = doorknob.save(new CompoundTag());
                compound.put("doorknob", itemTag);
            }
        });
        compound.putInt("doorwayLength", doorwayLength);
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

        if (compound.contains("doorwayLength")) {
            doorwayLength = compound.getInt("doorwayLength");
        }
        else {
            // If the doorway length is not stored in the block entity, fall back to legacy behavior.
            // Calculate the intended length with no efficiency modifier.
            doorwayLength = Optional.ofNullable(getDoorknob())
                    .map(d -> (int)Math.ceil(d.getDepth(0)))
                    // If we don't even know the type of doorknob, just fall back to the maximum possible value.
                    .orElse(MagicDoorknobItem.MAX_DOORWAY_LENGTH);
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

    /**
     * @return The length of the doorway.
     */
    public int getDoorwayLength() {
        return doorwayLength;
    }

    /**
     * @param doorwayLength The length of the doorway.
     */
    public void setDoorwayLength(int doorwayLength) {
        this.doorwayLength = doorwayLength;
    }
}
