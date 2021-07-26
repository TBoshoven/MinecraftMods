package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.resources.ResourceLocation;

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
    public void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer) {
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
    private static class RenderOffModelPlayer<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
        /**
         * The location of the texture to use.
         */
        private final ResourceLocation textureLocation;

        RenderOffModelPlayer(EntityRenderDispatcher renderManager, M model, ResourceLocation textureLocation) {
            super(renderManager, model, 0);

            // Can't use explicit types here because there is no way to check whether the type M implements these
            // interfaces or subclasses these classes due to type erasure and I don't want to work with too many
            // subclasses.
            if (model instanceof HeadedModel) {
                //noinspection unchecked,rawtypes
                addLayer(new CustomHeadLayer(this));
            }
            addLayer(new ElytraLayer<>(this));
            if (model instanceof ArmedModel) {
                //noinspection unchecked,rawtypes
                addLayer(new ItemInHandLayer(this));
            }
            if (model instanceof HumanoidModel) {
                //noinspection unchecked,rawtypes
                addLayer(new HumanoidArmorLayer(this, new HumanoidModel<>(0.5F), new HumanoidModel<>(1.0F)));
            }
            if (model instanceof PlayerModel) {
                //noinspection unchecked,rawtypes
                addLayer(new ArrowLayer(this));
                //noinspection unchecked,rawtypes
                addLayer(new ParrotOnShoulderLayer(this));
                //noinspection unchecked,rawtypes
                addLayer(new SpinAttackEffectLayer(this));
                //noinspection unchecked,rawtypes
                addLayer(new BeeStingerLayer(this));
            }
            this.textureLocation = textureLocation;
        }

        @Override
        public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
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
                        UseAnim itemUseAction = handItems[side].getUseAnimation();

                        if (itemUseAction == UseAnim.BLOCK) {
                            armPoses[side] = ArmPose.BLOCK;
                        } else if (itemUseAction == UseAnim.BOW) {
                            armPoses[side] = ArmPose.BOW_AND_ARROW;
                        }
                    }
                }
            }

            if (model instanceof HumanoidModel) {
                HumanoidModel<?> bipedModel = (HumanoidModel<?>) model;
                bipedModel.crouching = entity.isShiftKeyDown();
                if (entity.getMainArm() == HumanoidArm.RIGHT) {
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
    private static class ModelSkeletonPlayer<T extends LivingEntity> extends HumanoidModel<T> {
        @SuppressWarnings("AssignmentToSuperclassField")
        ModelSkeletonPlayer() {
            super(0, 0, 64, 32);

            // Skeletons have slightly different models, so we just apply the same modifications as the skeleton model
            // does.
            rightArm = new ModelPart(this, 40, 16);
            rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0);
            rightArm.setPos(-5.0F, 2.0F, 0.0F);
            leftArm = new ModelPart(this, 40, 16);
            leftArm.mirror = true;
            leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0);
            leftArm.setPos(5.0F, 2.0F, 0.0F);
            rightLeg = new ModelPart(this, 0, 16);
            rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0);
            rightLeg.setPos(-2.0F, 12.0F, 0.0F);
            leftLeg = new ModelPart(this, 0, 16);
            leftLeg.mirror = true;
            leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0);
            leftLeg.setPos(2.0F, 12.0F, 0.0F);
        }
    }
}
