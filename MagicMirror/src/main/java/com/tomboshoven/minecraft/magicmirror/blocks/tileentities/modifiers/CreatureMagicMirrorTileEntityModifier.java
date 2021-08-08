package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifierClient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreatureMagicMirrorTileEntityModifier extends MagicMirrorTileEntityModifier {
    /**
     * The object that modifies the reflection in the mirror to show the replacement armor.
     */
    @Nullable
    private CreatureReflectionModifier reflectionModifier;

    /**
     * @param modifier The modifier that applied this object to the tile entity.
     */
    public CreatureMagicMirrorTileEntityModifier(MagicMirrorModifier modifier) {
        super(modifier);
    }

    @Override
    public void remove(Level world, BlockPos pos) {
        Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SKELETON_SKULL, 1));
    }

    @Override
    public void activate(MagicMirrorCoreTileEntity tileEntity) {
        Reflection reflection = tileEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = createReflectionModifier();
            reflection.addModifier(reflectionModifier);
        }
    }

    private static CreatureReflectionModifier createReflectionModifier() {
        return DistExecutor.runForDist(
                () -> CreatureReflectionModifierClient::new,
                () -> CreatureReflectionModifier::new
        );
    }

    @Override
    public void deactivate(MagicMirrorCoreTileEntity tileEntity) {
        if (reflectionModifier != null) {
            Reflection reflection = tileEntity.getReflection();
            if (reflection != null) {
                reflection.removeModifier(reflectionModifier);
            }
        }
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorCoreTileEntity tileEntity, Player playerIn, InteractionHand hand) {
        // No behavior right now.
        return false;
    }
}
