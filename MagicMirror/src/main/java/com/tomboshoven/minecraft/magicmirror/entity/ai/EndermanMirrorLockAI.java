package com.tomboshoven.minecraft.magicmirror.entity.ai;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * AI to have Endermen become entranced by mirrors.
 */
public final class EndermanMirrorLockAI {
    private EndermanMirrorLockAI() {}

    @SubscribeEvent
    public static void onSpawn(LivingSpawnEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof EndermanEntity) {
            // Attach as an AI goal, same priority as the stare
            EndermanEntity endermanEntity = (EndermanEntity) entity;
            endermanEntity.goalSelector.addGoal(1, new EndermanMirrorLockGoal(endermanEntity));
        }
    }

    /**
     * Goal to make an Enderman look at a mirror if it's close enough.
     * While it's staring, it will not walk around and it will not attack players.
     */
    private static class EndermanMirrorLockGoal extends Goal {
        /**
         * The maximum distance between the Enderman and the mirror for it to be entranced.
         */
        private static final
        int MAX_MIRROR_DISTANCE = 8;

        /**
         * The Enderman owning this goal.
         * */
        private final EndermanEntity entity;
        /**
         * The mirror we're looking at. If null, the goal is not active.
          */
        @Nullable
        private BlockPos observedPos;

        /**
         * @param entity The Enderman owning this goal.
         */
        public EndermanMirrorLockGoal(EndermanEntity entity) {
            this.entity = entity;
            // While entranced, there is no moving around, looking, or retargeting.
            // Picking up and placing blocks, as well as teleportation, are still allowed.
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        }

        @Nullable
        private BlockPos findMirrorPos() {
            World world = entity.getEntityWorld();
            BlockPos entityPos = entity.getPosition();
            entityPos = entityPos.add(0, entity.getEyeHeight(), 0);

            // Check in all horizontal directions for mirrors that are close by.
            // Diagonal doesn't work.
            Block targetBlock = Blocks.MAGIC_MIRROR.get();
            for (Direction direction : Direction.values()) {
                if (direction.getAxis().isHorizontal()) {
                    Direction oppositeDirection = direction.getOpposite();
                    for (int i = 0; i < MAX_MIRROR_DISTANCE; ++i) {
                        BlockPos pos = entityPos.offset(direction, i);
                        BlockState blockState = world.getBlockState(pos);
                        // The mirror must be fully formed and facing the Enderman.
                        if (blockState.getBlock() == targetBlock) {
                            if (blockState.get(HorizontalBlock.HORIZONTAL_FACING) == oppositeDirection
                                    && blockState.get(MagicMirrorBlock.COMPLETE)) {
                                return pos;
                            }
                        }
                        else if (blockState.isSolid()) {
                            // A bit naive, but it should work well enough
                            break;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public boolean shouldExecute() {
            // Cache the position for use in the actual behavior
            observedPos = findMirrorPos();
            return observedPos != null;
        }

        @Override
        public void tick() {
            if (observedPos != null) {
                entity.getLookController().setLookPosition(observedPos.getX() + .5, observedPos.getY() + .5, observedPos.getZ() + .5);
            }
        }
    }
}
