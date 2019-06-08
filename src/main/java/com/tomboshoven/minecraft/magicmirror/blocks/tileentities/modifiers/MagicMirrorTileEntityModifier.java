package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A magic mirror modifier as applied to a tile entity.
 * Instead of using this directly, apply it using a MagicMirrorModifier instance.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MagicMirrorTileEntityModifier {
    /**
     * The modifier that applied this object to the tile entity.
     */
    private final MagicMirrorModifier modifier;

    /**
     * The number of ticks left in the cooldown.
     */
    private int cooldown;

    /**
     * @param modifier The modifier that applied this object to the tile entity.
     */
    MagicMirrorTileEntityModifier(MagicMirrorModifier modifier) {
        this.modifier = modifier;
    }

    /**
     * @return The name of this tile entity modifier. This should probably match the name of the modifier.
     */
    public String getName() {
        return modifier.getName();
    }

    /**
     * Write the modifier out to an NBT tag compound.
     * Including the name of the modifier is not necessary.
     *
     * @param nbt The NBT tag compound to write to.
     * @return The input compound, for chaining.
     */
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return nbt;
    }

    /**
     * Load modifier data from an NBT tag.
     *
     * @param nbt The NBT tag compound to read from.
     */
    public void readFromNBT(NBTTagCompound nbt) {
    }

    /**
     * Called when the tile entity is removed.
     * Can be used for things like spawning contained items back into the world.
     *
     * @param world The world that the tile entity is being removed from.
     * @param pos   The position of the removed block in the world.
     */
    public abstract void remove(World world, BlockPos pos);

    /**
     * Called when the modifier is attached to the tile entity.
     *
     * @param tileEntity The tile entity that is being modified.
     */
    public abstract void activate(TileEntityMagicMirrorBase tileEntity);

    /**
     * Called when the modifier is detached from the tile entity.
     *
     * @param tileEntity The tile entity that is being modified.
     */
    public abstract void deactivate(TileEntityMagicMirrorBase tileEntity);

    /**
     * Called when the player activates a magic mirror that is modified by this modifier.
     *
     * @param tileEntity The tile entity of the magic mirror that is being activated.
     * @param playerIn   The player that is activating the magic mirror.
     * @param hand       The hand that the player is using to activate the magic mirror.
     * @return Whether it was activated. If true, no other modifiers are evaluated.
     */
    public abstract boolean tryPlayerActivate(TileEntityMagicMirrorBase tileEntity, EntityPlayer playerIn, EnumHand hand);

    /**
     * When the modifier is used, this can be used to easily cool down, so it can't be activated all the time.
     *
     * @param ticks The number of ticks to wait before being cooled down completely.
     */
    void setCooldown(int ticks) {
        cooldown = ticks;
    }

    /**
     * @return Whether the modifier is cooling down.
     */
    boolean coolingDown() {
        return cooldown > 0;
    }

    /**
     * Cool down the modifier if necessary.
     */
    public void coolDown() {
        if (cooldown > 0) {
            --cooldown;
        }
    }
}
