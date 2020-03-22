package com.tomboshoven.minecraft.magicmirror.blocks.modifiers;

import com.google.common.collect.Maps;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifier;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A modifier for a magic mirror.
 * <p>
 * These are typically activated by right-clicking a mirror with an item.
 * In order to make a modifier available in the game, register it using the static register() method.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class MagicMirrorModifier {
    /**
     * A registry of all modifiers.
     */
    private static final Map<String, MagicMirrorModifier> MODIFIERS = Maps.newHashMap();

    /**
     * Register a new modifier into the game.
     *
     * @param modifier The modifier to be registered. Must have a unique name.
     */
    public static void register(MagicMirrorModifier modifier) {
        MODIFIERS.putIfAbsent(modifier.getName(), modifier);
    }

    /**
     * @return A collection of all modifiers that are registered in the game.
     */
    public static Collection<MagicMirrorModifier> getModifiers() {
        return Collections.unmodifiableCollection(MODIFIERS.values());
    }

    /**
     * Get a modifier by name, or null if no such modifier exists.
     *
     * @param name The name of the requested modifier.
     * @return A modifier with the provided name, or null if it does not exist.
     */
    @Nullable
    public static MagicMirrorModifier getModifier(String name) {
        return MODIFIERS.get(name);
    }

    /**
     * Helper method to get a magic mirror tile entity at the given position.
     *
     * @param worldIn The world containing the magic mirror.
     * @param pos     The position in the world of the block.
     * @return The tile entity, or null if it does not exist or is not a magic mirror tile entity.
     */
    @Nullable
    private static MagicMirrorBaseTileEntity getMagicMirrorTileEntity(IEnviromentBlockReader worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof MagicMirrorBaseTileEntity) {
            return (MagicMirrorBaseTileEntity) tileEntity;
        }
        return null;
    }

    /**
     * Find out whether the mirror at the given position already has a modifier of a given type.
     *
     * @param world        The world in which to check the block.
     * @param pos          The position of the mirror block to check.
     * @param modifierType The type of modifier to test for.
     * @return Whether the mirror at the given position has the given modifier.
     */
    private static boolean hasModifierOfType(IEnviromentBlockReader world, BlockPos pos, Class<? extends MagicMirrorModifier> modifierType) {
        MagicMirrorBaseTileEntity magicMirrorTileEntity = getMagicMirrorTileEntity(world, pos);
        if (magicMirrorTileEntity == null) {
            return false;
        }
        return magicMirrorTileEntity.getModifiers().stream().anyMatch(modifierType::isInstance);
    }

    /**
     * @return The name of the modifier.
     */
    public abstract String getName();

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
    public void apply(IEnviromentBlockReader worldIn, BlockPos pos, ItemStack heldItem) {
        MagicMirrorBaseTileEntity magicMirrorTileEntity = getMagicMirrorTileEntity(worldIn, pos);
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
    private void apply(MagicMirrorBaseTileEntity tileEntity, ItemStack heldItem) {
        tileEntity.addModifier(createTileEntityModifier());
        heldItem.shrink(1);
    }

    /**
     * Apply the modifier to the magic mirror as specified in an NBT tag.
     *
     * @param tileEntity The magic mirror tile entity to apply the modifier to.
     * @param nbt        The NBT tag to use for the modifier.
     */
    public void apply(MagicMirrorBaseTileEntity tileEntity, CompoundNBT nbt) {
        MagicMirrorTileEntityModifier magicMirrorTileEntityModifier = createTileEntityModifier();
        magicMirrorTileEntityModifier.read(nbt);
        tileEntity.addModifier(magicMirrorTileEntityModifier);
    }

    /**
     * @return A new instance of the tile entity modifier.
     */
    abstract MagicMirrorTileEntityModifier createTileEntityModifier();

    /**
     * Find out whether the mirror at the given position already has a modifier of the current type.
     *
     * @param world The world in which to check the block.
     * @param pos   The position of the mirror block to check.
     * @return Whether the mirror at the given position has the current modifier.
     */
    boolean hasModifierOfType(IEnviromentBlockReader world, BlockPos pos) {
        return hasModifierOfType(world, pos, getClass());
    }
}
