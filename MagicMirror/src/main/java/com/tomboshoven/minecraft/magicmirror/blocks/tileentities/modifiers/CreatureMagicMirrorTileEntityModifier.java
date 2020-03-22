package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifierClient;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    public void remove(World world, BlockPos pos) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SKELETON_SKULL, 1));
    }

    @Override
    public void activate(MagicMirrorBaseTileEntity tileEntity) {
        Reflection reflection = tileEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = createReflectionModifier();
            reflection.addModifier(reflectionModifier);
        }
    }

    private CreatureReflectionModifier createReflectionModifier() {
        return DistExecutor.runForDist(
                () -> CreatureReflectionModifierClient::new,
                () -> CreatureReflectionModifier::new
        );
    }

    @Override
    public void deactivate(MagicMirrorBaseTileEntity tileEntity) {
        if (reflectionModifier != null) {
            Reflection reflection = tileEntity.getReflection();
            if (reflection != null) {
                reflection.removeModifier(reflectionModifier);
            }
        }
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorBaseTileEntity tileEntity, PlayerEntity playerIn, Hand hand) {
        // No behavior right now.
        return false;
    }
}
