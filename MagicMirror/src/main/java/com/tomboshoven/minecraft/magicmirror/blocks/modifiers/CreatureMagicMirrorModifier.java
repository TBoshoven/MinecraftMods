package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.CreatureMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic mirror modifier that changes the appearance of the reflection to be another creature's.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreatureMagicMirrorModifier extends MagicMirrorModifier {
    /**
     * Return whether the given item is a skull that can be used to apply this modifier.
     *
     * @param item The item to test.
     * @return Whether the item is a skull of the right type.
     */
    private static boolean isSupportedSkull(ItemStack item) {
        // Only support skeleton skulls for now
        return item.getItem() == Items.SKELETON_SKULL && Items.SKELETON_SKULL.getDamage(item) == 0;
    }

    @Override
    public String getName() {
        return "creature";
    }

    @Override
    public boolean canModify(ItemStack heldItem, MagicMirrorCoreBlockEntity blockEntity) {
        return isSupportedSkull(heldItem) && !hasModifierOfType(blockEntity);
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(CompoundTag nbt) {
        MagicMirrorBlockEntityModifier teModifier = new CreatureMagicMirrorBlockEntityModifier(this, EntityType.SKELETON);
        teModifier.read(nbt);
        return teModifier;
    }

    @Override
    MagicMirrorBlockEntityModifier createBlockEntityModifier(ItemStack usedItem) {
        return new CreatureMagicMirrorBlockEntityModifier(this, EntityType.SKELETON);
    }
}
