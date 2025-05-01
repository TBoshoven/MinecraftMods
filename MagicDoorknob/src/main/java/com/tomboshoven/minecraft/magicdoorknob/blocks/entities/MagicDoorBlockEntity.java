package com.tomboshoven.minecraft.magicdoorknob.blocks.entities;

import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import static com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock.PART;

/**
 * Block entity for the magic door parts.
 */
public class MagicDoorBlockEntity extends MagicDoorwayPartBaseBlockEntity {
    public MagicDoorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MAGIC_DOOR.get(), pos, state);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (state.getValue(PART) == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
            // Spawn the doorknob
            Item doorknob = getDoorknob();
            if (doorknob != null && level != null) {
                Containers.dropItemStack(level, pos.getX(), pos.getY() - .5f, pos.getZ(), new ItemStack(doorknob, 1));
            }
        }
        super.preRemoveSideEffects(pos, state);
    }
}
