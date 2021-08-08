package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClient;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClientUpdater;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class BlockEntityMagicMirrorRendererBase {
    /**
     * Maximum distance for an entity to be rendered.
     * Used for fading the mirror image.
     */
    private static final double MAX_HORIZONTAL_DISTANCE_SQ = 8 * 8;
    private static final double MAX_VERTICAL_DISTANCE_SQ = 3 * 3;

    /**
     * Render a reflection.
     *
     * @param reflection The reflection to render.
     * @param pos The block position to render to.
     * @param facing The direction the reflection faces in.
     * @param poseStack The pose stack to use for rendering.
     * @param multiBufferSource The buffer source to use for rendering.
     */
    public void render(Reflection reflection, BlockPos pos, Direction facing, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        if (reflection instanceof ReflectionClient) {
            Entity reflected = reflection.getReflectedEntity();
            if (reflected != null) {
                Vec3 reflectedPos = reflected.position().add(.5, .5, .5);
                Vec3 distanceVector = reflectedPos.subtract(pos.getX(), pos.getY(), pos.getZ());

                ReflectionClientUpdater.markViewed((ReflectionClient) reflection);

                renderReflection((ReflectionClient) reflection, poseStack, multiBufferSource, facing, distanceVector);
            }
        }
    }

    abstract protected boolean isTop();

    /**
     * Render the reflection of an entity.
     *
     * @param reflection       The reflection to render.
     * @param matrixStack      The matrix stack to use for rendering.
     * @param renderTypeBuffer The buffer to render to.
     * @param facing           The direction in which the mirror part is facing.
     * @param distance         The distance between the mirror and the reflected subject; used for fading.
     */
    private void renderReflection(ReflectionClient reflection, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, Direction facing, Vec3 distance) {
        // The further away the subject is, the more faint the reflection
        double horizontalDistanceSq = distance.x * distance.x + distance.z * distance.z;
        double verticalDistanceSq = distance.y * distance.y;
        float reflectionAlpha = Math.max(0, Math.min(1f, 1.2f - (float) (horizontalDistanceSq / MAX_HORIZONTAL_DISTANCE_SQ) - (float) (verticalDistanceSq / MAX_VERTICAL_DISTANCE_SQ)));

        matrixStack.translate(.5, .5, .5);

        // Draw on top of the model instead of in the center of the block
        matrixStack.mulPose(Vector3f.YN.rotationDegrees(facing.toYRot()));
        matrixStack.translate(0, 0, -.4);

        VertexConsumer buffer = renderTypeBuffer.getBuffer(reflection.getRenderType());

        boolean top = isTop();
        float texTop = top ? 0f : .5f;
        float texBottom = top ? .5f : 1f;

        Matrix4f matrix = matrixStack.last().pose();

        // Draw a simple quad
        buffer.vertex(matrix, -.5f, -.5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(0, texBottom).endVertex();
        buffer.vertex(matrix, .5f, -.5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(1, texBottom).endVertex();
        buffer.vertex(matrix, .5f, .5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(1, texTop).endVertex();
        buffer.vertex(matrix, -.5f, .5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(0, texTop).endVertex();
    }
}
