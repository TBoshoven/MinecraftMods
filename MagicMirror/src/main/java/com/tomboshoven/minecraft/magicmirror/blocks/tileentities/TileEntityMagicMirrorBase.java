package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror.EnumPartType;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
    private Direction facing = Direction.NORTH;
    private EnumPartType part = EnumPartType.TOP;
    private boolean complete;

    public TileEntityMagicMirrorBase(TileEntityType<? extends TileEntityMagicMirrorBase> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void onLoad() {
        // Synchronize with blockstate; we need to know some of this in order to render the reflection.
        BlockState blockState = getWorld().getBlockState(getPos());
        if (blockState.getBlock() == Blocks.MAGIC_MIRROR) {
            facing = blockState.get(BlockStateProperties.HORIZONTAL_FACING);
            part = blockState.get(BlockMagicMirror.PART);
            complete = blockState.get(BlockMagicMirror.COMPLETE);
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
    public boolean tryActivate(PlayerEntity playerIn, Hand hand) {
        TileEntityMagicMirrorCore core = getCore();
        if (core != null) {
            return core.tryActivate(playerIn, hand);
        }
        return false;
    }

    /**
     * @return Which direction the mirror is facing in.
     */
    public Direction getFacing() {
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
