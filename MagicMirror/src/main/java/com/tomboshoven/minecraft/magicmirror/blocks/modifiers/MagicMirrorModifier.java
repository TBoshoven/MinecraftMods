package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorBaseBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.MagicMirrorBlockEntityModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * A modifier for a magic mirror.
 * <p>
 * These are typically activated by right-clicking a mirror with an item.
 * In order to make a modifier available in the game, register it using the static register() method.
 */
public abstract class MagicMirrorModifier extends ForgeRegistryEntry<MagicMirrorModifier> {
    /**
     * Helper method to get a magic mirror tile entity at the given position.
     *
     * @param worldIn The world containing the magic mirror.
     * @param pos     The position in the world of the block.
     * @return The tile entity, or null if it does not exist or is not a magic mirror tile entity.
     */
    @Nullable
    private static MagicMirrorBaseBlockEntity getMagicMirrorTileEntity(IBlockReader worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof MagicMirrorBaseBlockEntity) {
            return (MagicMirrorBaseBlockEntity) tileEntity;
        }
        return null;
    }

    /**
     * Find out whether the mirror at the given position already has a modifier of a given type.
     *
     * @param world    The world in which to check the block.
     * @param pos      The position of the mirror block to check.
     * @param modifier The modifier to test for.
     * @return Whether the mirror at the given position has the given modifier.
     */
    private static boolean hasModifierOfType(IBlockReader world, BlockPos pos, MagicMirrorModifier modifier) {
        MagicMirrorBaseBlockEntity magicMirrorTileEntity = getMagicMirrorTileEntity(world, pos);
        if (magicMirrorTileEntity == null) {
            return false;
        }
        return magicMirrorTileEntity.getModifiers().stream().anyMatch(teModifier -> teModifier.getModifier() == modifier);
    }

    /**
     * Whether the modifier can be applied when the player uses the held item on the magic mirror block.
     * <p>
     * This should also check constraints such as not modifying the same block twice etc.
     *
     * @param worldIn  The world containing the magic mirror.
     * @param pos      The position in the world of the block.
     * @param heldItem The item used on the block.
     * @return Whether the block can be modified in the given configuration.
     */
    public abstract boolean canModify(World worldIn, BlockPos pos, ItemStack heldItem);

    /**
     * Apply the modifier to the magic mirror.
     * <p>
     * This assumes that canModify(...) is true.
     *
     * @param worldIn  The world containing the magic mirror.
     * @param pos      The position in the world of the block.
     * @param heldItem The item used on the block.
     */
    public void apply(IBlockReader worldIn, BlockPos pos, ItemStack heldItem) {
        MagicMirrorBaseBlockEntity magicMirrorTileEntity = getMagicMirrorTileEntity(worldIn, pos);
        if (magicMirrorTileEntity == null) {
            return;
        }
        apply(magicMirrorTileEntity, heldItem);
    }

    /**
     * Apply the modifier to the magic mirror.
     * <p>
     * This assumes that canModify(...) is true.
     *
     * @param tileEntity The magic mirror tile entity to apply the modifier to.
     * @param heldItem   The item used on the block.
     */
    private void apply(MagicMirrorBaseBlockEntity tileEntity, ItemStack heldItem) {
        tileEntity.addModifier(createTileEntityModifier(heldItem));
    }

    /**
     * Apply the modifier to the magic mirror as specified in an NBT tag.
     *
     * @param tileEntity The magic mirror tile entity to apply the modifier to.
     * @param nbt        The NBT tag to use for the modifier.
     */
    public void apply(MagicMirrorBaseBlockEntity tileEntity, CompoundNBT nbt) {
        MagicMirrorBlockEntityModifier magicMirrorBlockEntityModifier = createTileEntityModifier(nbt);
        tileEntity.addModifier(magicMirrorBlockEntityModifier);
    }

    /**
     * @param nbt The NBT tag of the modifier.
     * @return A new instance of the tile entity modifier.
     */
    abstract MagicMirrorBlockEntityModifier createTileEntityModifier(CompoundNBT nbt);

    /**
     * @param usedItem The item used to attach the modifier.
     * @return A new instance of the tile entity modifier.
     */
    abstract MagicMirrorBlockEntityModifier createTileEntityModifier(ItemStack usedItem);

    /**
     * Find out whether the mirror at the given position already has a modifier of the current type.
     *
     * @param world The world in which to check the block.
     * @param pos   The position of the mirror block to check.
     * @return Whether the mirror at the given position has the current modifier.
     */
    boolean hasModifierOfType(IBlockReader world, BlockPos pos) {
        return hasModifierOfType(world, pos, this);
    }
}
