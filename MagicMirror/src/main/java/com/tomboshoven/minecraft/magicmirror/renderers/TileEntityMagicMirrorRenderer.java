package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror.EnumPartType;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Renderer for the Magic Mirror tile entity.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class TileEntityMagicMirrorRenderer extends TileEntitySpecialRenderer<TileEntityMagicMirrorBase> {
    /**
     * Maximum distance for an entity to be rendered.
     * Used for fading the mirror image.
     */
    private static final double MAX_HORIZONTAL_DISTANCE_SQ = 8 * 8;
    private static final double MAX_VERTICAL_DISTANCE_SQ = 3 * 3;

    @Override
    public void render(TileEntityMagicMirrorBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        if (te.isComplete()) {
            Reflection reflection = te.getReflection();

            if (reflection != null) {
                Entity reflected = reflection.getReflectedEntity();
                if (reflected != null) {
                    EnumPartType part = te.getPart();
                    EnumFacing facing = te.getFacing();

                    BlockPos tePos = te.getPos();
                    Vec3d reflectedPos = reflected.getPositionVector().add(.5, .5, .5);
                    Vec3d distanceVector = reflectedPos.subtract(tePos.getX(), tePos.getY(), tePos.getZ());

                    renderReflection(reflection, x, y, z, partialTicks, alpha, part, facing, distanceVector);
                }
            }
        }
    }

    /**
     * Render the reflection of an entity.
     *
     * @param reflection   The reflection to render.
     * @param x            The X coordinate of the tile entity.
     * @param y            The Y coordinate of the tile entity.
     * @param z            The Z coordinate of the tile entity.
     * @param partialTicks The partial ticks, used for smooth animations.
     * @param alpha        The alpha value to render with.
     * @param part         The part of the mirror to render.
     * @param facing       The direction in which the mirror part is facing.
     * @param distance     The distance between the mirror and the reflected subject; used for fading.
     */
    private static void renderReflection(Reflection reflection, double x, double y, double z, float partialTicks, float alpha, EnumPartType part, EnumFacing facing, Vec3d distance) {
        // Render the reflection.
        reflection.render(partialTicks);

        // Rebind original frame buffer.
        // This could be done in a nicer way, but I don't think a frame buffer stacking mechanism is available.
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);

        GlStateManager.pushMatrix();

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        // The further away the subject is, the more faint the reflection
        double horizontalDistanceSq = distance.x * distance.x + distance.z * distance.z;
        double verticalDistanceSq = distance.y * distance.y;
        float reflectionAlpha = Math.min(1f, 1.2f - (float) (horizontalDistanceSq / MAX_HORIZONTAL_DISTANCE_SQ) - (float) (verticalDistanceSq / MAX_VERTICAL_DISTANCE_SQ));
        GlStateManager.color(1f, 1f, 1f, alpha * reflectionAlpha);

        GlStateManager.translate(x + .5, y + .5, z + .5);

        // Draw on top of the model instead of in the center of the block
        GlStateManager.rotate(facing.getHorizontalAngle(), 0f, -1f, 0f);
        GlStateManager.translate(0, 0, -.4);

        GlStateManager.disableLighting();

        // Bind the texture we just rendered to
        reflection.bind();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        double texTop = part == EnumPartType.TOP ? 0 : .5;
        double texBottom = part == EnumPartType.TOP ? .5 : 1;

        // Draw a simple quad
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-.5, -.5, 0).tex(0, texBottom).endVertex();
        bufferbuilder.pos(.5, -.5, 0).tex(1, texBottom).endVertex();
        bufferbuilder.pos(.5, .5, 0).tex(1, texTop).endVertex();
        bufferbuilder.pos(-.5, .5, 0).tex(0, texTop).endVertex();
        tessellator.draw();

        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }
}
