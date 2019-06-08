package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierCreature;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierCreature.Factory;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.SidedProxy;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorTileEntityModifierCreature extends MagicMirrorTileEntityModifier {
    /**
     * Factory for creating the reflection modifier.
     */
    @SidedProxy(
            serverSide = "com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierCreature$Factory",
            clientSide = "com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierCreatureClient$Factory"
    )
    private static Factory reflectionModifierFactory;
    /**
     * The object that modifies the reflection in the mirror to show the replacement armor.
     */
    @Nullable
    private ReflectionModifierCreature reflectionModifier;

    /**
     * @param modifier The modifier that applied this object to the tile entity.
     */
    public MagicMirrorTileEntityModifierCreature(MagicMirrorModifier modifier) {
        super(modifier);
    }

    @Override
    public void remove(World world, BlockPos pos) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SKULL, 1, 0));
    }

    @Override
    public void activate(TileEntityMagicMirrorBase tileEntity) {
        Reflection reflection = tileEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = tileEntity.getWorld().isRemote ? reflectionModifierFactory.createClient() : reflectionModifierFactory.createServer();
            reflection.addModifier(reflectionModifier);
        }
    }

    @Override
    public void deactivate(TileEntityMagicMirrorBase tileEntity) {
        if (reflectionModifier != null) {
            Reflection reflection = tileEntity.getReflection();
            if (reflection != null) {
                reflection.removeModifier(reflectionModifier);
            }
        }
    }

    @Override
    public boolean tryPlayerActivate(TileEntityMagicMirrorBase tileEntity, EntityPlayer playerIn, EnumHand hand) {
        // No behavior right now.
        return false;
    }
}
