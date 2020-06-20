package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock.EnumPartType;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClient;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClientUpdater;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Renderer for the Magic Mirror tile entity.
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
        Reflection reflection = tileEntityIn.getReflection();

        if (reflection instanceof ReflectionClient) {
            Entity reflected = reflection.getReflectedEntity();
            if (reflected != null) {
                EnumPartType part = tileEntityIn.getPart();
                Direction facing = tileEntityIn.getFacing();

                BlockPos tePos = tileEntityIn.getPos();

                Vec3d reflectedPos = reflected.getPositionVector().add(.5, .5, .5);
                Vec3d distanceVector = reflectedPos.subtract(tePos.getX(), tePos.getY(), tePos.getZ());

                ReflectionClientUpdater.markViewed((ReflectionClient) reflection);

                renderReflection((ReflectionClient) reflection, matrixStackIn, bufferIn, part, facing, distanceVector);
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
    private static void renderReflection(ReflectionClient reflection, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, EnumPartType part, Direction facing, Vec3d distance) {
        // The further away the subject is, the more faint the reflection
        double horizontalDistanceSq = distance.x * distance.x + distance.z * distance.z;
        double verticalDistanceSq = distance.y * distance.y;
        float reflectionAlpha = Math.max(0, Math.min(1f, 1.2f - (float) (horizontalDistanceSq / MAX_HORIZONTAL_DISTANCE_SQ) - (float) (verticalDistanceSq / MAX_VERTICAL_DISTANCE_SQ)));

        matrixStack.translate(.5, .5, .5);

        // Draw on top of the model instead of in the center of the block
        matrixStack.rotate(Vector3f.YN.rotationDegrees(facing.getHorizontalAngle()));
        matrixStack.translate(0, 0, -.4);

        IVertexBuilder buffer = renderTypeBuffer.getBuffer(reflection.getRenderType());

        float texTop = part == EnumPartType.TOP ? 0f : .5f;
        float texBottom = part == EnumPartType.TOP ? .5f : 1f;

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        // Draw a simple quad
        buffer.pos(matrix, -.5f, -.5f, 0).color(1f, 1f, 1f, reflectionAlpha).tex(0, texBottom).endVertex();
        buffer.pos(matrix, .5f, -.5f, 0).color(1f, 1f, 1f, reflectionAlpha).tex(1, texBottom).endVertex();
        buffer.pos(matrix, .5f, .5f, 0).color(1f, 1f, 1f, reflectionAlpha).tex(1, texTop).endVertex();
        buffer.pos(matrix, -.5f, .5f, 0).color(1f, 1f, 1f, reflectionAlpha).tex(0, texTop).endVertex();
    }
}
