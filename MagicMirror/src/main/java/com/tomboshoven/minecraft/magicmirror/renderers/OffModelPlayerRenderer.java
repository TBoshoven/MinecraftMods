package com.tomboshoven.minecraft.magicmirror.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

/**
 * A renderer to render a player using a different (biped) model.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class OffModelPlayerRenderer<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
    /**
     * The location of the texture to use.
     */
    private final ResourceLocation textureLocation;

    OffModelPlayerRenderer(EntityRendererProvider.Context renderContext, M model, ResourceLocation textureLocation) {
        super(renderContext, model, 0);

        // Can't use explicit types here because there is no way to check whether the type M implements these
        // interfaces or subclasses these classes due to type erasure and I don't want to work with too many
        // subclasses.
        if (model instanceof HeadedModel) {
            //noinspection unchecked,rawtypes
            addLayer(new CustomHeadLayer(this, renderContext.getModelSet(), renderContext.getItemInHandRenderer()));
        }
        addLayer(new ElytraLayer<>(this, renderContext.getModelSet()));
        if (model instanceof ArmedModel) {
            //noinspection unchecked,rawtypes
            addLayer(new ItemInHandLayer(this, renderContext.getItemInHandRenderer()));
        }
        if (model instanceof HumanoidModel) {
            //noinspection unchecked,rawtypes
            addLayer(new HumanoidArmorLayer(this, new HumanoidModel<>(renderContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(renderContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), renderContext.getModelManager()));
        }
        if (model instanceof PlayerModel) {
            //noinspection unchecked,rawtypes
            addLayer(new ArrowLayer(renderContext, this));
            //noinspection unchecked,rawtypes
            addLayer(new ParrotOnShoulderLayer(this, renderContext.getModelSet()));
            //noinspection unchecked,rawtypes
            addLayer(new SpinAttackEffectLayer(this, renderContext.getModelSet()));
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
        HumanoidModel.ArmPose[] armPoses = {HumanoidModel.ArmPose.EMPTY, HumanoidModel.ArmPose.EMPTY};

        for (int side = 0; side < 2; ++side) {
            if (!handItems[side].isEmpty()) {
                armPoses[side] = HumanoidModel.ArmPose.ITEM;

                if (entity.getUseItemRemainingTicks() > 0) {
                    UseAnim itemUseAction = handItems[side].getUseAnimation();

                    if (itemUseAction == UseAnim.BLOCK) {
                        armPoses[side] = HumanoidModel.ArmPose.BLOCK;
                    } else if (itemUseAction == UseAnim.BOW) {
                        armPoses[side] = HumanoidModel.ArmPose.BOW_AND_ARROW;
                    }
                }
            }
        }

        if (model instanceof HumanoidModel<?> bipedModel) {
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

    static <T extends LivingEntity, M extends EntityModel<T>> EntityRendererProvider<T> createProvider(Function<EntityRendererProvider.Context, M> modelProvider, ResourceLocation textureLocation) {
        return renderContext -> new OffModelPlayerRenderer<>(renderContext, modelProvider.apply(renderContext), textureLocation);
    }
}
