package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClient;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClientUpdater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

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
     * @param reflection        The reflection to render.
     * @param pos               The block position to render to.
     * @param facing            The direction the reflection faces in.
     * @param poseStack         The pose stack to use for rendering.
     * @param multiBufferSource The buffer source to use for rendering.
     */
    public void render(Reflection reflection, BlockPos pos, Direction facing, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight) {
        if (reflection instanceof ReflectionClient) {
            Entity reflected = reflection.getReflectedEntity();
            if (reflected != null) {
                Vec3 reflectedPos = reflected.position().add(.5, .5, .5);
                Vec3 distanceVector = reflectedPos.subtract(pos.getX(), pos.getY(), pos.getZ());
                float alpha = getAlpha(distanceVector);

                ReflectionClientUpdater.markViewed((ReflectionClient) reflection);

                renderReflection((ReflectionClient) reflection, poseStack, multiBufferSource, facing, alpha, combinedLight);
                if (isTop()) {
                    Component nameTag = reflection.getNameTag();
                    if (nameTag != null) {
                        renderNameTag(nameTag, alpha, reflected.isDiscrete(), poseStack, multiBufferSource, facing, combinedLight);
                    }
                }
            }
        }
    }

    abstract protected boolean isTop();

    /**
     * The further away the subject is, the more faint the reflection.
     *
     * @param distance The distance vector between the mirror and the viewer
     * @return the alpha value to use when rendering
     */
    private static float getAlpha(Vec3 distance) {
        // The further away the subject is, the more faint the reflection
        double horizontalDistanceSq = distance.x * distance.x + distance.z * distance.z;
        double verticalDistanceSq = distance.y * distance.y;
        return Math.max(0, Math.min(1, 1.2f - (float) (horizontalDistanceSq / MAX_HORIZONTAL_DISTANCE_SQ) - (float) (verticalDistanceSq / MAX_VERTICAL_DISTANCE_SQ)));
    }

    /**
     * Render the reflection of an entity.
     *
     * @param reflection       The reflection to render.
     * @param poseStack        The matrix stack to use for rendering.
     * @param renderTypeBuffer The buffer to render to.
     * @param facing           The direction in which the mirror part is facing.
     * @param reflectionAlpha  The alpha (transparency) value to use when rendering.
     * @param combinedLight    The light information to pass along the rendering chain.
     */
    private void renderReflection(ReflectionClient reflection, PoseStack poseStack, MultiBufferSource renderTypeBuffer, Direction facing, float reflectionAlpha, int combinedLight) {
        if (!reflection.isAvailable()) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(.5, .5, .5);

        // Draw on top of the model instead of in the center of the block
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()));
        poseStack.translate(0, 0, -.4);

        VertexConsumer buffer = renderTypeBuffer.getBuffer(reflection.getRenderType());

        boolean top = isTop();
        float texTop = top ? 0f : .5f;
        float texBottom = top ? .5f : 1f;

        Matrix4f matrix = poseStack.last().pose();

        // Draw a simple quad
        buffer.vertex(matrix, -.5f, -.5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(0, texBottom).uv2(combinedLight).endVertex();
        buffer.vertex(matrix, .5f, -.5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(1, texBottom).uv2(combinedLight).endVertex();
        buffer.vertex(matrix, .5f, .5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(1, texTop).uv2(combinedLight).endVertex();
        buffer.vertex(matrix, -.5f, .5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(0, texTop).uv2(combinedLight).endVertex();
        poseStack.popPose();
    }

    /**
     * Render the reflection of an entity.
     *
     * @param nameTag           The name tag to render.
     * @param alpha             The alpha (transparency) value to use when rendering.
     * @param discrete          Whether to render the discrete version (entity is crouched).
     * @param poseStack         The matrix stack to use for rendering.
     * @param multiBufferSource The buffer to render to.
     * @param facing            The direction in which the mirror part is facing.
     * @param combinedLight     The light information to pass along the rendering chain
     */
    private void renderNameTag(Component nameTag, float alpha, boolean discrete, PoseStack poseStack, MultiBufferSource multiBufferSource, Direction facing, int combinedLight) {
        Minecraft minecraft = Minecraft.getInstance();
        float backgroundOpacity = minecraft.options.getBackgroundOpacity(0.25F);
        Font font = minecraft.font;

        float xOffset = (float)(-font.width(nameTag) / 2);

        poseStack.pushPose();
        poseStack.translate(.5, .5, .5);
        poseStack.mulPose(Axis.YN.rotationDegrees(facing.getOpposite().toYRot()));
        poseStack.translate(0, .375, .375);
        poseStack.scale(-0.0125f, -0.0125f, -0.0125f);
        Matrix4f matrix = poseStack.last().pose();

        int backgroundColor = (int)(backgroundOpacity * alpha * 255f) << 24;
        int darkForegroundColor = ((int)(alpha * 31f) << 24) | 0xffffff;
        int lightForegroundColor = ((int)(alpha * 255f) << 24) | 0xffffff;
        font.drawInBatch(nameTag, xOffset, 0, darkForegroundColor, false, matrix, multiBufferSource, discrete ? Font.DisplayMode.NORMAL : Font.DisplayMode.SEE_THROUGH, backgroundColor, combinedLight);
        if (!discrete) {
            font.drawInBatch(nameTag, xOffset, 0, lightForegroundColor, false, matrix, multiBufferSource, Font.DisplayMode.NORMAL, 0, combinedLight);
        }

        poseStack.popPose();
    }
}
