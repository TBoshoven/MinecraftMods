package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.renderers.OffModelPlayerRenderers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Reflection renderer modifier that replaces the rendered entity by a different one.
 * <p>
 * Currently, only skeletons are supported.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreatureReflectionRendererModifier extends ReflectionRendererModifier {
    final EntityType<?> entityType;

    /**
     * @param baseRenderer The renderer that is being proxied.
     */
    public CreatureReflectionRendererModifier(ReflectionRendererBase baseRenderer, EntityType<?> entityType) {
        super(baseRenderer);
        this.entityType = entityType;
    }

    @Override
    public void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer) {
        EntityRenderer<? extends Entity> originalRenderer = getRenderer();
        EntityRenderer<?> replacementRenderer = OffModelPlayerRenderers.getInstance().get(entityType);
        if (replacementRenderer != null) {
            setRenderer(replacementRenderer);
        }
        super.render(facing, partialTicks, renderTypeBuffer);
        setRenderer(originalRenderer);
    }
}
