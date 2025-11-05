package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.ReflectionManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

abstract class BlockEntityMagicMirrorRendererBase<E extends BlockEntity> implements BlockEntityRenderer<E, BlockEntityMagicMirrorRendererBase.RenderState> {
    /**
     * Maximum distance for an entity to be rendered.
     * Used for fading the mirror image.
     */
    private static final double MAX_HORIZONTAL_DISTANCE_SQ = 8 * 8;
    private static final double MAX_VERTICAL_DISTANCE_SQ = 3 * 3;

    /**
     * Item model resolver, used for rendering items in reflections
     */
    private final ItemModelResolver itemModelResolver;

    /**
     * @param context The render context for the reflection.
     */
    BlockEntityMagicMirrorRendererBase(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    /**
     * @return Whether this is the top (true) or bottom (false) part of the reflection.
     */
    abstract protected boolean isTop();

    /**
     * @return The render context for the reflection.
     */
    Reflection.RenderContext renderContext() {
        return new Reflection.RenderContext(itemModelResolver);
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    protected void extractRenderState(MagicMirrorCoreBlockEntity blockEntity, RenderState renderState) {
        renderState.facing = blockEntity.getBlockState().getValue(FACING);
        Reflection reflection = ReflectionManager.reflectionForRendering(blockEntity, renderContext());
        renderState.reflection = reflection;
        Entity reflected = reflection.getReflectedEntity();
        if (reflected != null) {
            Vec3 reflectedPos = reflected.position().add(.5, .5, .5);
            BlockPos pos = blockEntity.getBlockPos();
            renderState.distance = reflectedPos.subtract(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void submit(RenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        Reflection reflection = renderState.reflection;
        if (reflection == null || !reflection.isAvailable()) {
            return;
        }

        // The further away the subject is, the more faint the reflection
        Vec3 distance = renderState.distance;
        double horizontalDistanceSq = distance.x * distance.x + distance.z * distance.z;
        double verticalDistanceSq = distance.y * distance.y;
        float reflectionAlpha = Math.max(0, Math.min(1f, 1.2f - (float) (horizontalDistanceSq / MAX_HORIZONTAL_DISTANCE_SQ) - (float) (verticalDistanceSq / MAX_VERTICAL_DISTANCE_SQ)));

        poseStack.translate(.5, .5, .5);

        // Draw on top of the model instead of in the center of the block
        poseStack.mulPose(Axis.YN.rotationDegrees(renderState.facing.toYRot()));
        poseStack.translate(0, 0, -.4);

        boolean top = isTop();
        float texTop = top ? 0f : .5f;
        float texBottom = top ? .5f : 1f;

        int lightCoords = renderState.lightCoords;

        submitNodeCollector.submitCustomGeometry(poseStack, reflection.getRenderType(), (PoseStack.Pose pose, VertexConsumer consumer) -> {
            consumer.addVertex(pose, -.5f, -.5f, 0).setColor(1f, 1f, 1f, reflectionAlpha).setUv(0, texBottom).setLight(lightCoords);
            consumer.addVertex(pose, .5f, -.5f, 0).setColor(1f, 1f, 1f, reflectionAlpha).setUv(1, texBottom).setLight(lightCoords);
            consumer.addVertex(pose, .5f, .5f, 0).setColor(1f, 1f, 1f, reflectionAlpha).setUv(1, texTop).setLight(lightCoords);
            consumer.addVertex(pose, -.5f, .5f, 0).setColor(1f, 1f, 1f, reflectionAlpha).setUv(0, texTop).setLight(lightCoords);
        });
    }

    static class RenderState extends BlockEntityRenderState {
        Direction facing;
        @Nullable Reflection reflection;
        Vec3 distance;
    }
}
