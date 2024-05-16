package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.CreatureMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A magic mirror modifier that changes the appearance of the reflection to be another creature's.
 */
public class CreatureMagicMirrorModifier extends MagicMirrorModifier {
    /**
     * Return whether the given item is a skull that can be used to apply this modifier.
     *
     * @param item The item to test.
     * @return Whether the item is a skull of the right type.
     */
    private static boolean isSupportedSkull(ItemStack item) {
        // Only support skeleton skulls for now
        return item.getItem() == Items.SKELETON_SKULL;
    }

    /**
     * Check whether the given entity type is supported by the mirror.
     * This can be used as a safety check to prevent crashes upon bad NBT data.
     *
     * @param entityType The entity type to check.
     * @return whether the provided entity type is supported by the modifier.
     */
    public static boolean isSupportedEntityType(EntityType<?> entityType) {
        // Only skeletons are supported for now
        return entityType == EntityType.SKELETON;
    }

    /**
     * Get the default entity type to use when we have no information.
     * This can be used for recovery upon encountering bad data.
     *
     * @return the entity type to use when we don't have the required information.
     */
    public static EntityType<?> getDefaultEntityType() {
        return EntityType.SKELETON;
    }

    @Override
    public boolean canModify(World worldIn, BlockPos pos, ItemStack heldItem) {
        return isSupportedSkull(heldItem) && !hasModifierOfType(worldIn, pos);
    }

    @Override
    MagicMirrorBlockEntityModifier createTileEntityModifier(CompoundNBT nbt) {
        return new CreatureMagicMirrorBlockEntityModifier(this, nbt);
    }

    @Override
    MagicMirrorBlockEntityModifier createTileEntityModifier(ItemStack usedItem) {
        // Only one entity type is supported for now
        EntityType<?> entityType = getDefaultEntityType();
        return new CreatureMagicMirrorBlockEntityModifier(this, usedItem.split(1), entityType);
    }
}
