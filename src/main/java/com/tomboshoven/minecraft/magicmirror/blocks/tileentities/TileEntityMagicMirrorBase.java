package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror.EnumPartType;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * Base class for magic mirror multiblock.
 * Provides a common interface to to the core tile entity.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityMagicMirrorBase extends TileEntity {
    // Some values copied from the blockstate
    private EnumFacing facing = EnumFacing.NORTH;
    private EnumPartType part = EnumPartType.TOP;
    private boolean complete;

    @Override
    public void onLoad() {
        // Synchronize with blockstate; we need to know some of this in order to render the reflection.
        IBlockState blockState = getWorld().getBlockState(getPos());
        if (blockState.getBlock() == Blocks.blockMagicMirror) {
            facing = blockState.getValue(BlockHorizontal.FACING);
            part = blockState.getValue(BlockMagicMirror.PART);
            complete = blockState.getValue(BlockMagicMirror.COMPLETE);
        }
    }

    /**
     * @return The core block, if it exists.
     */
    @Nullable
    protected abstract TileEntityMagicMirrorCore getCore();

    /**
     * @return The reflection in the mirror.
     */
    @Nullable
    public Reflection getReflection() {
        TileEntityMagicMirrorCore core = getCore();
        if (core != null) {
            return core.getReflection();
        }
        return null;
    }

    /**
     * @return A list of all the current modifiers of the mirror.
     */
    public List<MagicMirrorTileEntityModifier> getModifiers() {
        TileEntityMagicMirrorCore core = getCore();
        if (core != null) {
            return core.getModifiers();
        }
        return Collections.emptyList();
    }

    /**
     * Add a modifier to the mirror.
     *
     * @param modifier The modifier to add. Must be verified to be applicable.
     */
    public void addModifier(MagicMirrorTileEntityModifier modifier) {
        TileEntityMagicMirrorCore core = getCore();
        if (core != null) {
            core.addModifier(modifier);
        }
    }

    /**
     * Remove all modifiers from the tile entity.
     * This is used when the block is destroyed, and may have side effects such as spawning item entities into the
     * world.
     *
     * @param worldIn The world containing the removed block.
     * @param pos     The position of the block that was removed.
     */
    public void removeModifiers(World worldIn, BlockPos pos) {
        TileEntityMagicMirrorCore core = getCore();
        if (core != null) {
            core.removeModifiers(worldIn, pos);
        }
    }

    /**
     * Called when a player activates a magic mirror tile entity with this modifier.
     *
     * @param playerIn The player that activated the mirror.
     * @param hand     The hand used by the player to activate the mirror.
     * @return Whether activation of the modifier was successful.
     */
    public boolean tryActivate(EntityPlayer playerIn, EnumHand hand) {
        TileEntityMagicMirrorCore core = getCore();
        if (core != null) {
            return core.tryActivate(playerIn, hand);
        }
        return false;
    }

    /**
     * @return Which direction the mirror is facing in.
     */
    public EnumFacing getFacing() {
        return facing;
    }

    /**
     * @return Which part of the mirror this tile entity belongs to.
     */
    public EnumPartType getPart() {
        return part;
    }

    /**
     * @return Whether the mirror is completely constructed.
     */
    public boolean isComplete() {
        return complete;
    }
}
