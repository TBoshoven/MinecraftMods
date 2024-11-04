package com.tomboshoven.minecraft.magicmirror.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixing for manipulating the main render target.
 */
@Mixin(Minecraft.class)
public interface MinecraftRenderTargetMixin {
    @Accessor
    RenderTarget getMainRenderTarget();

    @Mutable
    @Accessor
    void setMainRenderTarget(RenderTarget renderTarget);
}
