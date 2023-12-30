package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock.EnumPartType;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorCoreTileEntity;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionClient;
import com.tomboshoven.minecraft.magicmirror.reflection.ReflectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

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

    @Override
    public void render(MagicMirrorBaseTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);

        MagicMirrorCoreTileEntity core = tileEntityIn.getCore();

        if (core != null) {
            ReflectionClient reflection = ReflectionManager.reflectionForRendering(core);
            Entity reflected = reflection.getReflectedEntity();
            if (reflected != null) {
                EnumPartType part = tileEntityIn.getPart();
                Direction facing = tileEntityIn.getFacing();

                BlockPos tePos = tileEntityIn.getBlockPos();

                Vec3d reflectedPos = reflected.getCommandSenderWorldPosition().add(.5, .5, .5);
                Vec3d distanceVector = reflectedPos.subtract(tePos.getX(), tePos.getY(), tePos.getZ());

                renderReflection(reflection, x, y, z, partialTicks, part, facing, distanceVector);
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
     * @param part         The part of the mirror to render.
     * @param facing       The direction in which the mirror part is facing.
     * @param distance     The distance between the mirror and the reflected subject; used for fading.
     */
    private static void renderReflection(Reflection reflection, double x, double y, double z, float partialTicks, EnumPartType part, Direction facing, Vec3d distance) {
        // Render the reflection.
        reflection.render(partialTicks);

        // Rebind original frame buffer.
        // This could be done in a nicer way, but I don't think a frame buffer stacking mechanism is available.
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);

        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // The further away the subject is, the more faint the reflection
        double horizontalDistanceSq = distance.x * distance.x + distance.z * distance.z;
        double verticalDistanceSq = distance.y * distance.y;
        float reflectionAlpha = Math.min(1f, 1.2f - (float) (horizontalDistanceSq / MAX_HORIZONTAL_DISTANCE_SQ) - (float) (verticalDistanceSq / MAX_VERTICAL_DISTANCE_SQ));
        GlStateManager.color4f(1f, 1f, 1f, reflectionAlpha);

        GlStateManager.translated(x + .5, y + .5, z + .5);

        // Draw on top of the model instead of in the center of the block
        GlStateManager.rotatef(facing.toYRot(), 0f, -1f, 0f);
        GlStateManager.translated(0, 0, -.4);

        GlStateManager.disableLighting();

        // Bind the texture we just rendered to
        reflection.bind();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();

        double texTop = part == EnumPartType.TOP ? 0 : .5;
        double texBottom = part == EnumPartType.TOP ? .5 : 1;

        // Draw a simple quad
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.vertex(-.5, -.5, 0).uv(0, texBottom).endVertex();
        bufferbuilder.vertex(.5, -.5, 0).uv(1, texBottom).endVertex();
        bufferbuilder.vertex(.5, .5, 0).uv(1, texTop).endVertex();
        bufferbuilder.vertex(-.5, .5, 0).uv(0, texTop).endVertex();
        tessellator.end();

        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }
}
