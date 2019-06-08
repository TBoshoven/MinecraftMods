package com.tomboshoven.minecraft.magicmirror.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers.MagicMirrorTileEntityModifierArmor.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.renderers.ReflectionRendererBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A modifier for a reflection renderer which replaces the armor that is worn by the reflected entity.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ReflectionRendererModifierArmor extends ReflectionRendererModifier {
    /**
     * The armor to show instead of the actual armor.
     */
    private final ReplacementArmor replacementArmor;

    /**
     * @param baseRenderer     The renderer that is being proxied.
     * @param replacementArmor The armor to show instead of the actual armor.
     */
    public ReflectionRendererModifierArmor(ReflectionRendererBase baseRenderer, ReplacementArmor replacementArmor) {
        super(baseRenderer);
        this.replacementArmor = replacementArmor;
    }

    @Override
    public void render(float facing, float partialTicks) {
        NonNullList<ItemStack> inventoryToSwap = null;
        Entity entity = getEntity();
        if (entity instanceof EntityPlayer) {
            inventoryToSwap = ((EntityPlayer) entity).inventory.armorInventory;
        }

        // Simply swap out the armor inventory twice
        replacementArmor.swap(inventoryToSwap);

        super.render(facing, partialTicks);

        replacementArmor.swap(inventoryToSwap);
    }
}
