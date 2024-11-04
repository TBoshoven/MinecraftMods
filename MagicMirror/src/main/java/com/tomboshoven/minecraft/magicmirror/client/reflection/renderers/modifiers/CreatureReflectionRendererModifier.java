package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.client.renderers.OffModelRenderer;
import com.tomboshoven.minecraft.magicmirror.client.renderers.OffModelRenderers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Reflection renderer modifier that replaces the rendered entity by a different one.
 * Supported entities are defined in OffModelRenderers.
 */
public class CreatureReflectionRendererModifier<E extends Entity> extends ReflectionRendererModifier<E> {
    final EntityType<?> entityType;

    /**
     * @param baseRenderer The renderer that is being proxied.
     * @param entityType   The entity that we'd like to render.
     */
    public CreatureReflectionRendererModifier(ReflectionRendererBase<E> baseRenderer, EntityType<?> entityType) {
        super(baseRenderer);
        this.entityType = entityType;
        // getType doesn't include a specifier, but it's reasonable to assume this
        @SuppressWarnings("unchecked") EntityType<? super E> thisEntityType = (EntityType<? super E>) getEntity().getType();
        OffModelRenderer<? super E, ? extends EntityRenderState, ?, ?, ?>.Renderer replacementRenderer = OffModelRenderers.getInstance().get(thisEntityType, entityType);
        if (replacementRenderer != null) {
            // TODO: Rather than replacing, this can be done during init
            replaceRenderer(replacementRenderer);
        }
    }
}
