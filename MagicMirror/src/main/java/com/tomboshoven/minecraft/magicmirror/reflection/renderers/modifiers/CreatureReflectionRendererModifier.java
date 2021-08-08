package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import com.tomboshoven.minecraft.magicmirror.renderers.Renderers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

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
