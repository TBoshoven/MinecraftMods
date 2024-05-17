package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock.EnumPartType;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Base class for magic mirror multiblock.
 * Provides a common interface to to the core tile entity.
 */
public abstract class MagicMirrorBaseBlockEntity extends TileEntity {
    MagicMirrorBaseBlockEntity(TileEntityType<? extends MagicMirrorBaseBlockEntity> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    /**
     * @return The core block, if it exists.
     */
    @Nullable
    public abstract MagicMirrorCoreBlockEntity getCore();

    /**
     * @return A list of all the current modifiers of the mirror.
     */
    public List<MagicMirrorBlockEntityModifier> getModifiers() {
        MagicMirrorCoreBlockEntity core = getCore();
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
    public void addModifier(MagicMirrorBlockEntityModifier modifier) {
        MagicMirrorCoreBlockEntity core = getCore();
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
        MagicMirrorCoreBlockEntity core = getCore();
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
        MagicMirrorCoreBlockEntity core = getCore();
        if (core != null) {
            return core.tryActivate(playerIn, hand);
        }
        return false;
    }

    /**
     * @return Which direction the mirror is facing in.
     */
    public Direction getFacing() {
        return getBlockState().getValue(MagicMirrorBlock.FACING);
    }

    /**
     * @return Which part of the mirror this tile entity belongs to.
     */
    public EnumPartType getPart() {
        return getBlockState().getValue(MagicMirrorBlock.PART);
    }
}