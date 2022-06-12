package com.tomboshoven.minecraft.magicmirror.renderers;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OffModelPlayerRenderers implements ResourceManagerReloadListener {
    /**
     * The map of entity types to off-model renderer provider.
     */
    final static Map<EntityType<?>, EntityRendererProvider<?>> RENDERER_PROVIDERS = new HashMap<>();

    static {
        RENDERER_PROVIDERS.put(
                EntityType.SKELETON,
                OffModelPlayerRenderer.createProvider(
                        context -> new HumanoidModel<>(context.bakeLayer(ModelLayers.SKELETON)),
                        new ResourceLocation("textures/entity/skeleton/skeleton.png")
                ));
    }

    /**
     * The map from entity type to off-model renderers.
     * This is reloaded as part of regular reloads.
     */
    final Map<EntityType<?>, EntityRenderer<?>> RENDERERS = new HashMap<>();

    private static OffModelPlayerRenderers INSTANCE;

    /**
     * @return The singleton instance of this class.
     */
    public static OffModelPlayerRenderers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OffModelPlayerRenderers();
        }
        return INSTANCE;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Minecraft minecraft = Minecraft.getInstance();
        EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        EntityModelSet entityModels = minecraft.getEntityModels();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        ItemInHandRenderer itemInHandRenderer = entityRenderDispatcher.getItemInHandRenderer();
        Font font = minecraft.font;
        EntityRendererProvider.Context context = new EntityRendererProvider.Context(entityRenderDispatcher, itemRenderer, blockRenderer, itemInHandRenderer, resourceManager, entityModels, font);

        RENDERER_PROVIDERS.forEach((entityType, rendererProvider) -> RENDERERS.put(entityType, rendererProvider.create(context)));
    }

    /**
     * Get the off-model renderer to use for rendering the given entity type.
     *
     * @param entityType The type of entity to get the renderer for.
     * @return The off-model renderer for the given entity type.
     */
    @Nullable
    public EntityRenderer<?> get(EntityType<?> entityType) {
        return RENDERERS.get(entityType);
    }
}
