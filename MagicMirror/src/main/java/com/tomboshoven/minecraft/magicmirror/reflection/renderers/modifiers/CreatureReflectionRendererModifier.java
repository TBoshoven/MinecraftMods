package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.BipedModel.ArmPose;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Reflection renderer modifier that replaces the rendered entity by a different one.
 * <p>
 * Currently, only skeletons are supported.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreatureReflectionRendererModifier extends ReflectionRendererModifier {
    /**
     * The renderer to use instead of the default one.
     */
    private final EntityRenderer<? extends Entity> replacementRenderer;

    /**
     * @param baseRenderer The renderer that is being proxied.
     */
    public CreatureReflectionRendererModifier(ReflectionRendererBase baseRenderer) {
        super(baseRenderer);
        replacementRenderer = new RenderOffModelPlayer<>(Minecraft.getInstance().getEntityRenderDispatcher(), new ModelSkeletonPlayer<>(), new ResourceLocation("textures/entity/skeleton/skeleton.png"));
    }

    @Override
    public void render(float facing, float partialTicks, IRenderTypeBuffer renderTypeBuffer) {
        EntityRenderer<? extends Entity> originalRenderer = getRenderer();
        setRenderer(replacementRenderer);
        super.render(facing, partialTicks, renderTypeBuffer);
        setRenderer(originalRenderer);
    }

    /**
     * A renderer to render a player using a different (biped) model.
     */
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class RenderOffModelPlayer<T extends LivingEntity, M extends EntityModel<T>> extends LivingRenderer<T, M> {
        /**
         * The location of the texture to use.
         */
        private final ResourceLocation textureLocation;

        RenderOffModelPlayer(EntityRendererManager renderManager, M model, ResourceLocation textureLocation) {
            super(renderManager, model, 0);

            // Can't use explicit types here because there is no way to check whether the type M implements these
            // interfaces or subclasses these classes due to type erasure and I don't want to work with too many
            // subclasses.
            if (model instanceof IHasHead) {
                //noinspection unchecked,rawtypes
                addLayer(new HeadLayer(this));
            }
            addLayer(new ElytraLayer<>(this));
            if (model instanceof IHasArm) {
                //noinspection unchecked,rawtypes
                addLayer(new HeldItemLayer(this));
            }
            if (model instanceof BipedModel) {
                //noinspection unchecked,rawtypes
                addLayer(new BipedArmorLayer(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
            }
            if (model instanceof PlayerModel) {
                //noinspection unchecked,rawtypes
                addLayer(new ArrowLayer(this));
                //noinspection unchecked,rawtypes
                addLayer(new ParrotVariantLayer(this));
                //noinspection unchecked,rawtypes
                addLayer(new SpinAttackEffectLayer(this));
                //noinspection unchecked,rawtypes
                addLayer(new BeeStingerLayer(this));
            }
            this.textureLocation = textureLocation;
        }

        @Override
        public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
            setArmPoses(entityIn);
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }

        /**
         * Properly set the arm poses based on the player.
         * This code is adapted from RenderPlayer.
         *
         * @param entity The entity to take the arm poses from.
         */
        private void setArmPoses(T entity) {
            M model = getModel();

            ItemStack[] handItems = {entity.getMainHandItem(), entity.getOffhandItem()};
            ArmPose[] armPoses = {ArmPose.EMPTY, ArmPose.EMPTY};

            for (int side = 0; side < 2; ++side) {
                if (!handItems[side].isEmpty()) {
                    armPoses[side] = ArmPose.ITEM;

                    if (entity.getUseItemRemainingTicks() > 0) {
                        UseAction itemUseAction = handItems[side].getUseAnimation();

                        if (itemUseAction == UseAction.BLOCK) {
                            armPoses[side] = ArmPose.BLOCK;
                        } else if (itemUseAction == UseAction.BOW) {
                            armPoses[side] = ArmPose.BOW_AND_ARROW;
                        }
                    }
                }
            }

            if (model instanceof BipedModel) {
                BipedModel<?> bipedModel = (BipedModel<?>) model;
                bipedModel.crouching = entity.isShiftKeyDown();
                if (entity.getMainArm() == HandSide.RIGHT) {
                    bipedModel.rightArmPose = armPoses[0];
                    bipedModel.leftArmPose = armPoses[1];
                } else {
                    bipedModel.rightArmPose = armPoses[1];
                    bipedModel.leftArmPose = armPoses[0];
                }
            }
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return textureLocation;
        }
    }

    /**
     * A player model that renders a skeleton.
     * Skeletons don't use the default biped model, but the actual skeleton model requires a mob entity.
     */
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class ModelSkeletonPlayer<T extends LivingEntity> extends BipedModel<T> {
        @SuppressWarnings("AssignmentToSuperclassField")
        ModelSkeletonPlayer() {
            super(0, 0, 64, 32);

            // Skeletons have slightly different models, so we just apply the same modifications as the skeleton model
            // does.
            rightArm = new ModelRenderer(this, 40, 16);
            rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0);
            rightArm.setPos(-5.0F, 2.0F, 0.0F);
            leftArm = new ModelRenderer(this, 40, 16);
            leftArm.mirror = true;
            leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0);
            leftArm.setPos(5.0F, 2.0F, 0.0F);
            rightLeg = new ModelRenderer(this, 0, 16);
            rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0);
            rightLeg.setPos(-2.0F, 12.0F, 0.0F);
            leftLeg = new ModelRenderer(this, 0, 16);
            leftLeg.mirror = true;
            leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0);
            leftLeg.setPos(2.0F, 12.0F, 0.0F);
        }
    }
}
