package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.renderers.Renderers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

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
        replacementRenderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(Renderers.REFLECTION_ENTITY_TYPE);
    }

    @Override
    public void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer) {
        EntityRenderer<? extends Entity> originalRenderer = getRenderer();
        setRenderer(replacementRenderer);
        super.render(facing, partialTicks, renderTypeBuffer);
        setRenderer(originalRenderer);
    }
}
