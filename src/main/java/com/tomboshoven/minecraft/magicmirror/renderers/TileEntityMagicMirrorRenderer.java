package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.BlockMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorPart;
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
import net.minecraft.util.math.Vec3d;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityMagicMirrorRenderer extends TileEntitySpecialRenderer<TileEntityMagicMirrorPart> {

    private static final double MAX_DISTANCE = 8;

    @Override
    public void render(TileEntityMagicMirrorPart te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        if (te.isComplete()) {
            renderReflection(te, x, y, z, partialTicks, alpha);
        }
    }

    private void renderReflection(TileEntityMagicMirrorPart te, double x, double y, double z, float partialTicks, float alpha) {
        Reflection reflection = te.getReflection();

        if (reflection == null) {
            // Nothing to do if we somehow don't have a reflection
            return;
        }

        Entity reflected = reflection.getReflected();

        if (reflected == null) {
            // Nothing to do if we're not reflecting anything
            return;
        }

        Vec3d reflectedPos = reflected.getPositionVector();
        double distanceSq = te.getPos().distanceSq(reflectedPos.x, reflectedPos.y, reflectedPos.z);

        // Render the reflection.
        reflection.render(partialTicks);

        // Rebind original frame buffer.
        // This could be done in a nicer way, but I don't think a frame buffer stacking mechanism is available.
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);

        GlStateManager.pushMatrix();

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        // The further away the subject is, the more faint the reflection
        float reflectionAlpha = Math.min(1f, 1.2f - (float) (distanceSq / (MAX_DISTANCE * MAX_DISTANCE)));
        GlStateManager.color(1f, 1f, 1f, alpha * reflectionAlpha);

        GlStateManager.translate(x + .5, y + .5, z + .5);

        // Draw on top of the model instead of in the center of the block
        EnumFacing facing = te.getFacing();
        GlStateManager.rotate(facing.getHorizontalAngle(), 0f, -1f, 0f);
        GlStateManager.translate(0, 0, -.4);

        GlStateManager.disableLighting();

        // Bind the texture we just rendered to
        reflection.bind();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        BlockMagicMirror.EnumPartType part = te.getPart();
        double texTop = part == BlockMagicMirror.EnumPartType.TOP ? 0 : .5;
        double texBottom = part == BlockMagicMirror.EnumPartType.TOP ? .5 : 1;

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
