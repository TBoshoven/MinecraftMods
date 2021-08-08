package com.tomboshoven.minecraft.magicmirror.renderers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Manager of all renderers in the mod.
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Renderers {
    public static EntityType<LivingEntity> REFLECTION_ENTITY_TYPE = EntityType.Builder.<LivingEntity>createNothing(MobCategory.MISC).build("off_model_player");

    static EntityRendererProvider<LivingEntity> REFLECTION_ENTITY_RENDERER_PROVIDER = OffModelPlayerRenderer.createProvider(
            context -> new HumanoidModel<>(context.bakeLayer(ModelLayers.SKELETON)),
            new ResourceLocation("textures/entity/skeleton/skeleton.png")
    );

    private Renderers() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Renderers::registerRenderers);
    }

    private static void registerRenderers(ModelRegistryEvent event) {
        BlockEntityRenderers.register(TileEntities.MAGIC_MIRROR_CORE.get(), BlockEntityMagicMirrorCoreRenderer::new);
        BlockEntityRenderers.register(TileEntities.MAGIC_MIRROR_PART.get(), BlockEntityMagicMirrorPartRenderer::new);

        EntityRenderers.register(REFLECTION_ENTITY_TYPE, REFLECTION_ENTITY_RENDERER_PROVIDER);
    }
}
