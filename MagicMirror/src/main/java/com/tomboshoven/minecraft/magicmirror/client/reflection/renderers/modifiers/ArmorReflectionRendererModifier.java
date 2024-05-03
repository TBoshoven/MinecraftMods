package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.ArmorMagicMirrorTileEntityModifier.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * A modifier for a reflection renderer which replaces the armor that is worn by the reflected entity.
 */
public class ArmorReflectionRendererModifier extends ReflectionRendererModifier {
    /**
     * The armor to show instead of the actual armor.
     */
    private final ReplacementArmor replacementArmor;

    /**
     * @param baseRenderer     The renderer that is being proxied.
     * @param replacementArmor The armor to show instead of the actual armor.
     */
    public ArmorReflectionRendererModifier(ReflectionRendererBase baseRenderer, ReplacementArmor replacementArmor) {
        super(baseRenderer);
        this.replacementArmor = replacementArmor;
    }

    @Override
    public void render(float facing, float partialTicks, IRenderTypeBuffer renderTypeBuffer) {
        Entity entity = getEntity();
        if (entity instanceof PlayerEntity) {
            NonNullList<ItemStack> inventoryToSwap = ((PlayerEntity) entity).inventory.armor;

            // Simply swap out the armor inventory twice
            replacementArmor.swap(inventoryToSwap);

            super.render(facing, partialTicks, renderTypeBuffer);

            replacementArmor.swap(inventoryToSwap);
        }
    }
}