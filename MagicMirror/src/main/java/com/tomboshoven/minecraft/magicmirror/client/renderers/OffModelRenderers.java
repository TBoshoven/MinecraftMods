package com.tomboshoven.minecraft.magicmirror.client.renderers;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Off-model renderer definitions.
 */
public class OffModelRenderers {
    /**
     * A combination source, target.
     * Serves as a key for our map.
     *
     * @param source The entity type of the actual rendered entity.
     * @param target The entity type of the shown entity.
     */
    public record Mapping(EntityType<?> source, EntityType<?> target) {
    }

    /**
     * The map from entity type to off-model renderers.
     * Should only be manipulated through putRenderer and get, to enforce type constraints.
     */
    final private static Map<Mapping, OffModelRenderer<?, ?, ?, ?, ?>.Renderer> RENDERERS = new HashMap<>();

    private static OffModelRenderers INSTANCE;

    /**
     * @return The singleton instance of this class.
     */
    public static OffModelRenderers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OffModelRenderers();
        }
        return INSTANCE;
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(OffModelRenderers::addLayers);
    }

    private static void addLayers(EntityRenderersEvent.AddLayers event) {
        // Convenient way to hook into entity renderer mapping after it is loaded
        RENDERERS.clear();
        EntityRendererProvider.Context context = event.getContext();
        // Skin doesn't really matter since we're not actually rendering; we just care about the render state initialization and updates
        EntityRenderer<Player, PlayerRenderState> playerRenderer = event.getSkin(PlayerSkin.Model.WIDE);

        if (playerRenderer != null) {
            EntityRenderer<Skeleton, SkeletonRenderState> skeletonRenderer = event.getRenderer(EntityType.SKELETON);
            if (skeletonRenderer != null) {
                // Skeleton doesn't have any properties of interest over humanoid
                putRenderer(EntityType.PLAYER, EntityType.SKELETON, new OffModelRenderer<Player, PlayerRenderState, Skeleton, SkeletonRenderState, SkeletonRenderStateHolder<PlayerRenderState>>(playerRenderer, skeletonRenderer, SkeletonRenderStateHolder::new, new HumanoidRendererMapper<>()).create(context));
            }
        }
    }

    /**
     * Add a new renderer to RENDERERS.
     *
     * @param source         The entity type of the actual rendered entity.
     * @param target         The entity type of the shown entity.
     * @param renderer       The off-model renderer that does the actual rendering.
     * @param <SourceEntity> The type of the actual rendered entity.
     * @param <TargetEntity> The type of the shown entity.
     */
    private static <SourceEntity extends Entity, TargetEntity extends Entity> void putRenderer(EntityType<SourceEntity> source, EntityType<TargetEntity> target, OffModelRenderer<?, ? extends EntityRenderState, TargetEntity, ?, ?>.Renderer renderer) {
        RENDERERS.put(new Mapping(source, target), renderer);
    }

    /**
     * Safely obtain a renderer from RENDERERS.
     *
     * @param source         The entity type of the actual rendered entity.
     * @param target         The entity type of the shown entity.
     * @param <SourceEntity> The type of the actual rendered entity.
     * @param <TargetEntity> The type of the shown entity.
     * @return The requested off-model renderer, or null if none exists.
     */
    @Nullable
    public <SourceEntity extends Entity, TargetEntity extends Entity> OffModelRenderer<SourceEntity, ? extends EntityRenderState, TargetEntity, ?, ?>.Renderer get(EntityType<SourceEntity> source, EntityType<TargetEntity> target) {
        //noinspection unchecked
        return (OffModelRenderer<SourceEntity, ? extends EntityRenderState, TargetEntity, ?, ?>.Renderer) RENDERERS.get(new Mapping(source, target));
    }

    /**
     * Render state holder for skeleton target entities.
     *
     * @param <SourceState> The source state to hold.
     */
    private static class SkeletonRenderStateHolder<SourceState extends EntityRenderState> extends SkeletonRenderState implements OffModelRenderer.RenderStateHolder<SourceState> {
        SourceState source;

        SkeletonRenderStateHolder(SourceState source) {
            super();
            this.source = source;
        }

        @Override
        public SourceState getSource() {
            return source;
        }
    }

    /**
     * Mapper for entities based on the base entity render state,
     *
     * @param <Source> The type of the source state to read from.
     * @param <Target> The type of the target state to write to.
     */
    private static class EntityRendererMapper<Source extends EntityRenderState, Target extends EntityRenderState> implements OffModelRenderer.RenderStateMapper<Source, Target> {
        @Override
        public void mapRenderState(Source sourceState, Target targetState) {
            targetState.ageInTicks = sourceState.ageInTicks;
            targetState.boundingBoxWidth = sourceState.boundingBoxWidth;
            targetState.boundingBoxHeight = sourceState.boundingBoxHeight;
            targetState.eyeHeight = sourceState.eyeHeight;
            targetState.isInvisible = sourceState.isInvisible;
            targetState.isDiscrete = sourceState.isDiscrete;
            targetState.displayFireAnimation = sourceState.displayFireAnimation;
            targetState.partialTick = sourceState.partialTick;
        }
    }

    /**
     * Mapper for entities based on the living entity render state,
     *
     * @param <Source> The type of the source state to read from.
     * @param <Target> The type of the target state to write to.
     */
    private static class LivingEntityRendererMapper<Source extends LivingEntityRenderState, Target extends LivingEntityRenderState> extends EntityRendererMapper<Source, Target> {
        @Override
        public void mapRenderState(Source sourceState, Target targetState) {
            super.mapRenderState(sourceState, targetState);

            targetState.bodyRot = sourceState.bodyRot;
            targetState.yRot = sourceState.yRot;
            targetState.xRot = sourceState.xRot;
            targetState.deathTime = sourceState.deathTime;
            targetState.walkAnimationPos = sourceState.walkAnimationPos;
            targetState.walkAnimationSpeed = sourceState.walkAnimationSpeed;
            targetState.wornHeadAnimationPos = sourceState.wornHeadAnimationPos;
            targetState.scale = sourceState.scale;
            targetState.ageScale = sourceState.scale;
            targetState.isUpsideDown = sourceState.isUpsideDown;
            targetState.isFullyFrozen = sourceState.isFullyFrozen;
            targetState.isBaby = sourceState.isBaby;
            targetState.isInWater = sourceState.isInWater;
            targetState.isAutoSpinAttack = sourceState.isAutoSpinAttack;
            targetState.hasRedOverlay = sourceState.hasRedOverlay;
            targetState.isInvisibleToPlayer = sourceState.isInvisibleToPlayer;
            targetState.appearsGlowing = sourceState.appearsGlowing;
            targetState.pose = sourceState.pose;
            targetState.headItemModel = sourceState.headItemModel;
            targetState.headItem = sourceState.headItem;
            targetState.mainArm = sourceState.mainArm;
            targetState.rightHandItemModel = sourceState.rightHandItemModel;
            targetState.rightHandItem = sourceState.rightHandItem;
            targetState.leftHandItemModel = sourceState.leftHandItemModel;
            targetState.leftHandItem = sourceState.leftHandItem;
        }
    }

    /**
     * Mapper for entities based on the humanoid entity render state,
     *
     * @param <Source> The type of the source state to read from.
     * @param <Target> The type of the target state to write to.
     */
    private static class HumanoidRendererMapper<Source extends HumanoidRenderState, Target extends HumanoidRenderState> extends LivingEntityRendererMapper<Source, Target> {
        @Override
        public void mapRenderState(Source sourceState, Target targetState) {
            super.mapRenderState(sourceState, targetState);

            targetState.swimAmount = sourceState.swimAmount;
            targetState.attackTime = sourceState.attackTime;
            targetState.speedValue = sourceState.speedValue;
            targetState.maxCrossbowChargeDuration = sourceState.maxCrossbowChargeDuration;
            targetState.ticksUsingItem = sourceState.ticksUsingItem;
            targetState.attackArm = sourceState.attackArm;
            targetState.useItemHand = sourceState.useItemHand;
            targetState.isCrouching = sourceState.isCrouching;
            targetState.isFallFlying = sourceState.isFallFlying;
            targetState.isVisuallySwimming = sourceState.isVisuallySwimming;
            targetState.isPassenger = sourceState.isPassenger;
            targetState.isUsingItem = sourceState.isUsingItem;
            targetState.elytraRotX = sourceState.elytraRotX;
            targetState.elytraRotY = sourceState.elytraRotY;
            targetState.elytraRotZ = sourceState.elytraRotZ;
            targetState.chestItem = sourceState.chestItem;
            targetState.legsItem = sourceState.legsItem;
            targetState.feetItem = sourceState.feetItem;
        }
    }
}
