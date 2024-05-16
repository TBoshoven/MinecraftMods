package com.tomboshoven.minecraft.magicmirror.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock.EnumPartType;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.ReflectionManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Renderer for the Magic Mirror tile entity.
 */
class TileEntityMagicMirrorRenderer extends TileEntityRenderer<MagicMirrorBaseTileEntity> {
    /**
     * Maximum distance for an entity to be rendered.
     * Used for fading the mirror image.
     */
    private static final double MAX_HORIZONTAL_DISTANCE_SQ = 8 * 8;
    private static final double MAX_VERTICAL_DISTANCE_SQ = 3 * 3;

    TileEntityMagicMirrorRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MagicMirrorBaseTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        MagicMirrorCoreTileEntity core = tileEntityIn.getCore();

        if (core != null) {
            Reflection reflection = ReflectionManager.reflectionForRendering(core);
            Entity reflected = reflection.getReflectedEntity();
            if (reflected != null) {
                EnumPartType part = tileEntityIn.getPart();
                Direction facing = tileEntityIn.getFacing();

                BlockPos tePos = tileEntityIn.getBlockPos();

                Vec3d reflectedPos = reflected.getCommandSenderWorldPosition().add(.5, .5, .5);
                Vec3d distanceVector = reflectedPos.subtract(tePos.getX(), tePos.getY(), tePos.getZ());

                renderReflection(reflection, matrixStackIn, bufferIn, part, facing, distanceVector);
            }
        }
    }

    /**
     * Render the reflection of an entity.
     *
     * @param reflection       The reflection to render.
     * @param matrixStack      The matrix stack to use for rendering.
     * @param renderTypeBuffer The buffer to render to.
     * @param part             The part of the mirror to render.
     * @param facing           The direction in which the mirror part is facing.
     * @param distance         The distance between the mirror and the reflected subject; used for fading.
     */
    private static void renderReflection(Reflection reflection, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, EnumPartType part, Direction facing, Vec3d distance) {
        // The further away the subject is, the more faint the reflection
        double horizontalDistanceSq = distance.x * distance.x + distance.z * distance.z;
        double verticalDistanceSq = distance.y * distance.y;
        float reflectionAlpha = Math.max(0, Math.min(1f, 1.2f - (float) (horizontalDistanceSq / MAX_HORIZONTAL_DISTANCE_SQ) - (float) (verticalDistanceSq / MAX_VERTICAL_DISTANCE_SQ)));

        matrixStack.translate(.5, .5, .5);

        // Draw on top of the model instead of in the center of the block
        matrixStack.mulPose(Vector3f.YN.rotationDegrees(facing.toYRot()));
        matrixStack.translate(0, 0, -.4);

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(reflection.getRenderType());

        float texTop = part == EnumPartType.TOP ? 0f : .5f;
        float texBottom = part == EnumPartType.TOP ? .5f : 1f;

        Matrix4f matrix = matrixStack.last().pose();

        // Draw a simple quad
        buffer.vertex(matrix, -.5f, -.5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(0, texBottom).endVertex();
        buffer.vertex(matrix, .5f, -.5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(1, texBottom).endVertex();
        buffer.vertex(matrix, .5f, .5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(1, texTop).endVertex();
        buffer.vertex(matrix, -.5f, .5f, 0).color(1f, 1f, 1f, reflectionAlpha).uv(0, texTop).endVertex();
    }
}
