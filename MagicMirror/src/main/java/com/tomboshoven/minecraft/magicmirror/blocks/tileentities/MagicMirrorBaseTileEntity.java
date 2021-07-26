package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock.EnumPartType;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
public abstract class MagicMirrorBaseTileEntity extends BlockEntity {
    MagicMirrorBaseTileEntity(BlockEntityType<? extends MagicMirrorBaseTileEntity> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    /**
     * @return The core block, if it exists.
     */
    @Nullable
    protected abstract MagicMirrorCoreTileEntity getCore();

    /**
     * @return The reflection in the mirror.
     */
    @Nullable
    public Reflection getReflection() {
        MagicMirrorCoreTileEntity core = getCore();
        if (core != null) {
            return core.getReflection();
        }
        return null;
    }

    /**
     * @return A list of all the current modifiers of the mirror.
     */
    public List<MagicMirrorTileEntityModifier> getModifiers() {
        MagicMirrorCoreTileEntity core = getCore();
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
        MagicMirrorCoreTileEntity core = getCore();
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
    public void removeModifiers(Level worldIn, BlockPos pos) {
        MagicMirrorCoreTileEntity core = getCore();
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
    public boolean tryActivate(Player playerIn, InteractionHand hand) {
        MagicMirrorCoreTileEntity core = getCore();
        if (core != null) {
            return core.tryActivate(playerIn, hand);
        }
        return false;
    }

    /**
     * @return Which direction the mirror is facing in.
     */
    public Direction getFacing() {
        return getBlockState().getValue(HorizontalDirectionalBlock.FACING);
    }

    /**
     * @return Which part of the mirror this tile entity belongs to.
     */
    public EnumPartType getPart() {
        return getBlockState().getValue(MagicMirrorBlock.PART);
    }
}
