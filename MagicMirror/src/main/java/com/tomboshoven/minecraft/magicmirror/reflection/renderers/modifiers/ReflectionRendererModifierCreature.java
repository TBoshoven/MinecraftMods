package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Reflection renderer modifier that replaces the rendered entity by a different one.
 * <p>
 * Currently, only skeletons are supported.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ReflectionRendererModifierCreature extends ReflectionRendererModifier {
    /**
     * The renderer to use instead of the default one.
     */
    private final EntityRenderer<? extends Entity> replacementRenderer;

    /**
     * @param baseRenderer The renderer that is being proxied.
     */
    public ReflectionRendererModifierCreature(ReflectionRendererBase baseRenderer) {
        super(baseRenderer);
        replacementRenderer = new RenderOffModelPlayer(Minecraft.getInstance().getRenderManager(), new ModelSkeletonPlayer(), new ResourceLocation("textures/entity/skeleton/skeleton.png"));
    }

    @Override
    public void render(float facing, float partialTicks) {
        EntityRenderer<? extends Entity> originalRenderer = getRenderer();
        setRenderer(replacementRenderer);
        super.render(facing, partialTicks);
        setRenderer(originalRenderer);
    }

    /**
     * A renderer to render a player using a different (biped) model.
     */
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class RenderOffModelPlayer extends RenderLivingBase<AbstractClientPlayer> {
        /**
         * The location of the texture to use.
         */
        private final ResourceLocation textureLocation;

        RenderOffModelPlayer(RenderManager renderManager, ModelBiped model, ResourceLocation textureLocation) {
            super(renderManager, model, 0);
            addLayer(new LayerCustomHead(model.bipedHead));
            addLayer(new LayerElytra(this));
            addLayer(new LayerHeldItem(this));
            addLayer(new LayerHeldItem(this));
            addLayer(new LayerBipedArmor(this));
            this.textureLocation = textureLocation;
        }

        @Override
        public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
            setArmPoses(entity);
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }

        /**
         * Properly set the arm poses based on the player.
         * This code is adapted from RenderPlayer.
         *
         * @param entity The entity to take the arm poses from.
         */
        private void setArmPoses(AbstractClientPlayer entity) {
            ModelBiped model = (ModelBiped) getMainModel();
            model.isSneak = entity.isSneaking();

            ItemStack[] handItems = {entity.getHeldItemMainhand(), entity.getHeldItemOffhand()};
            ArmPose[] armPoses = {ArmPose.EMPTY, ArmPose.EMPTY};

            for (int side = 0; side < 2; ++side) {
                if (!handItems[side].isEmpty()) {
                    armPoses[side] = ArmPose.ITEM;

                    if (entity.getItemInUseCount() > 0) {
                        EnumAction itemUseAction = handItems[side].getItemUseAction();

                        if (itemUseAction == EnumAction.BLOCK) {
                            armPoses[side] = ArmPose.BLOCK;
                        } else if (itemUseAction == EnumAction.BOW) {
                            armPoses[side] = ArmPose.BOW_AND_ARROW;
                        }
                    }
                }
            }

            if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
                model.rightArmPose = armPoses[0];
                model.leftArmPose = armPoses[1];
            } else {
                model.rightArmPose = armPoses[1];
                model.leftArmPose = armPoses[0];
            }
        }

        @Nullable
        @Override
        protected ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
            return textureLocation;
        }
    }

    /**
     * A player model that renders a skeleton.
     * Skeletons don't use the default biped model, but the actual skeleton model requires a skeleton entity.
     */
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class ModelSkeletonPlayer extends ModelBiped {
        @SuppressWarnings("AssignmentToSuperclassField")
        ModelSkeletonPlayer() {
            super(0, 0, 64, 32);

            // Skeletons have slightly different models, so we just apply the same modifications as the skeleton model
            // does.
            bipedRightArm = new ModelRenderer(this, 40, 16);
            bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0);
            bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
            bipedLeftArm = new ModelRenderer(this, 40, 16);
            bipedLeftArm.mirror = true;
            bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0);
            bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            bipedRightLeg = new ModelRenderer(this, 0, 16);
            bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0);
            bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
            bipedLeftLeg = new ModelRenderer(this, 0, 16);
            bipedLeftLeg.mirror = true;
            bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0);
            bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        }
    }
}
