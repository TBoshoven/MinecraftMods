package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DyeMagicMirrorBlockEntityModifier extends ItemBasedMagicMirrorBlockEntityModifier {
    /**
     * @param modifier The modifier that applied this object to the block entity.
     */
    public DyeMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier, item);
    }

    public DyeMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt) {
        super(modifier, nbt);
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorCoreBlockEntity blockEntity, Player playerIn, InteractionHand hand) {
        // No behavior right now.
        return false;
    }

    public float[] getColor() {
        float[] color;
        if (item.getItem() instanceof DyeItem dyeItem) {
            float[] textureDiffuseColors = dyeItem.getDyeColor().getTextureDiffuseColors();
            color = new float[]{textureDiffuseColors[0], textureDiffuseColors[1], textureDiffuseColors[2], 1f};
        } else {
            color = new float[]{1f, 1f, 1f, 1f};
        }
        return color;
    }
}
