package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
    public void render(float facing, float partialTicks, MultiBufferSource renderTypeBuffer) {
        Entity entity = getEntity();
        if (entity instanceof Player) {
            NonNullList<ItemStack> inventoryToSwap = ((Player) entity).getInventory().armor;

            // Simply swap out the armor inventory twice
            replacementArmor.swap(inventoryToSwap);

            super.render(facing, partialTicks, renderTypeBuffer);

            replacementArmor.swap(inventoryToSwap);
        }
    }
}
