package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

import static com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock.PART;

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
    protected void saveInternal(ValueOutput output) {
        super.saveInternal(output);
        doorknob.ifPresent(doorknob -> output.store("doorknob", ItemStack.CODEC, doorknob));
    }

    @Override
    protected void loadInternal(ValueInput input) {
        super.loadInternal(input);
        doorknob = input.read("doorknob", ItemStack.CODEC);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        doorknob.ifPresentOrElse(doorknob -> {
            // Spawn the held doorknob
            if (level != null) {
                Containers.dropItemStack(level, pos.getX(), pos.getY() - .5f, pos.getZ(), doorknob);
            }
        }, () -> {
            // Backward compatibility: spawn a simple version of the doorknob type
            if (state.getValue(PART) == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
                // Spawn the doorknob
                Item doorknob = getDoorknob();
                if (doorknob != null && level != null) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY() - .5f, pos.getZ(), new ItemStack(doorknob, 1));
                }
            }
        });
        super.preRemoveSideEffects(pos, state);
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
