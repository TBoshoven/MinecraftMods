package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier.ReplacementArmor;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * A modifier for a reflection renderer which replaces the armor that is worn by the reflected entity.
 */
public class ArmorReflectionRendererModifier<E extends Entity> extends ReflectionRendererModifier<E> {
    /**
     * The armor to show instead of the actual armor.
     */
    private final ReplacementArmor replacementArmor;

    /**
     * The item renderer for the replacement armor.
     */
    private final ItemRenderer itemRenderer;

    /**
     * @param baseRenderer     The renderer that is being proxied.
     * @param replacementArmor The armor to show instead of the actual armor.
     * @param context          The render context.
     */
    public ArmorReflectionRendererModifier(ReflectionRendererBase<E> baseRenderer, ReplacementArmor replacementArmor, Reflection.RenderContext context) {
        super(baseRenderer);
        this.replacementArmor = replacementArmor;
        this.itemRenderer = context.itemRenderer();
    }

    @Override
    public EntityRenderState updateState(float partialTicks) {
        EntityRenderState state = super.updateState(partialTicks);
        E entity = getEntity();
        if (state instanceof LivingEntityRenderState livingState && entity instanceof LivingEntity livingEntity) {
            ItemStack headItem = replacementArmor.getBySlot(EquipmentSlot.HEAD).copy();
            livingState.headItem = headItem;
            livingState.headItemModel = itemRenderer.resolveItemModel(headItem, livingEntity, ItemDisplayContext.HEAD);

            if (state instanceof HumanoidRenderState humanoidState) {
                humanoidState.chestItem = replacementArmor.getBySlot(EquipmentSlot.CHEST).copy();
                humanoidState.legsItem = replacementArmor.getBySlot(EquipmentSlot.LEGS).copy();
                humanoidState.feetItem = replacementArmor.getBySlot(EquipmentSlot.FEET).copy();
            }
        }
        return state;
    }
}
