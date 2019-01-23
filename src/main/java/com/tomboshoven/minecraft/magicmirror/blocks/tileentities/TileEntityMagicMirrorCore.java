package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * The tile entity for the bottom mirror block; this is the block that has all the reflection logic.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityMagicMirrorCore extends TileEntityMagicMirrorPart implements ITickable {
    /**
     * Number of ticks between updating who we're reflecting
     */
    private static final int REFLECTION_UPDATE_INTERVAL = 10;

    private Reflection reflection = new Reflection();

    // Start the update counter at its max, so we update on the first tick.
    private int reflectionUpdateCounter = REFLECTION_UPDATE_INTERVAL;

    @Override
    public void onLoad() {
        super.onLoad();

        reflection.setFacing(getFacing().getHorizontalAngle());
    }

    /**
     * Find all players that can be reflected.
     *
     * @param world:       The world in which the tile entity exists.
     * @param ownPosition: The position of the tile entity in the world.
     * @return A list of players that are candidates for reflecting.
     */
    private List<AbstractClientPlayer> findReflectablePlayers(World world, BlockPos ownPosition) {
        // TODO: Add facing limitations
        AxisAlignedBB scanBB = new AxisAlignedBB(ownPosition.add(-10, -4, -10), ownPosition.add(10, 4, 10));
        return world.getEntitiesWithinAABB(AbstractClientPlayer.class, scanBB);
    }

    /**
     * Find the best player to reflect.
     *
     * @param world:       The world in which the tile entity exists.
     * @param ownPosition: The position of the tile entity in the world.
     * @return A player to reflect, or null if there are no valid candidates.
     */
    @Nullable
    private AbstractClientPlayer findPlayerToReflect(World world, BlockPos ownPosition) {
        List<AbstractClientPlayer> players = findReflectablePlayers(world, ownPosition);
        if (players.isEmpty()) {
            return null;
        }
        return Collections.min(players, (playerA, playerB) ->
                (int) (playerA.getPosition().distanceSq(ownPosition) - playerB.getPosition().distanceSq(ownPosition))
        );
    }

    /**
     * Update the reflection to show the closest player.
     */
    private void updateReflection() {
        World world = getWorld();

        AbstractClientPlayer playerToReflect = findPlayerToReflect(world, getPos());

        if (playerToReflect == null) {
            reflection.stopReflecting();
        } else {
            reflection.setEntityToReflect(playerToReflect);
        }
    }

    @Override
    public void update() {
        if (reflectionUpdateCounter++ == REFLECTION_UPDATE_INTERVAL) {
            reflectionUpdateCounter = 0;
            updateReflection();
        }
        // Make sure we re-render each full tick, to make the partialTick optimization work
        reflection.forceRerender();
    }

    @Override
    public void onChunkUnload() {
        // Stop reflecting to unload the textures and frame buffer
        reflection.stopReflecting();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // Stop reflecting, but prepare to restart the next tick, in case we get validated again
        // We need to make sure that we stop reflecting when the tile entity is destroyed, so we don't leak any frame
        // buffers and textures
        reflection.stopReflecting();
        reflectionUpdateCounter = REFLECTION_UPDATE_INTERVAL;
    }

    @Nullable
    @Override
    public Reflection getReflection() {
        return reflection;
    }
}
