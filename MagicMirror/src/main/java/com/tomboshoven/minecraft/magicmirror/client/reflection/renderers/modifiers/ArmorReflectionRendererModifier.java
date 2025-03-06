package com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers.ArmorMagicMirrorBlockEntityModifier;
import com.tomboshoven.minecraft.magicmirror.client.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.client.reflection.renderers.ReflectionRendererBase;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;

/**
 * A modifier for a reflection renderer which replaces the armor that is worn by the reflected entity.
 */
public class ArmorReflectionRendererModifier<E extends Entity> extends ReflectionRendererModifier<E> {
    /**
     * The armor to show instead of the actual armor.
     */
    private final ArmorMagicMirrorBlockEntityModifier.ReplacementArmor replacementArmor;

    /**
     * The item model resolver for the replacement armor.
     */
    private final ItemModelResolver itemModelResolver;

    /**
     * @param baseRenderer     The renderer that is being proxied.
     * @param replacementArmor The armor to show instead of the actual armor.
     * @param context          The render context.
     */
    public ArmorReflectionRendererModifier(ReflectionRendererBase<E> baseRenderer, ArmorMagicMirrorBlockEntityModifier.ReplacementArmor replacementArmor, Reflection.RenderContext context) {
        super(baseRenderer);
        this.replacementArmor = replacementArmor;
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public EntityRenderState updateState(float partialTicks) {
        EntityRenderState state = super.updateState(partialTicks);
        E entity = getEntity();
        if (state instanceof LivingEntityRenderState livingState && entity instanceof LivingEntity livingEntity) {
            ItemStack headEquipment = replacementArmor.getBySlot(EquipmentSlot.HEAD).copy();

            // Replicate regular armor rendering logic, taking into account special skull rendering logic
            livingState.wornHeadType = null;
            livingState.wornHeadProfile = null;
            boolean isSkull = false;
            if (headEquipment.getItem() instanceof BlockItem blockitem) {
                if (blockitem.getBlock() instanceof AbstractSkullBlock skullBlock) {
                    isSkull = true;
                    livingState.wornHeadType = skullBlock.getType();
                    livingState.wornHeadProfile = headEquipment.get(DataComponents.PROFILE);
                }
            }
            boolean shouldRenderHead = HumanoidArmorLayer.shouldRender(headEquipment, EquipmentSlot.HEAD);
            if (isSkull || shouldRenderHead) {
                livingState.headItem.clear();
            } else {
                itemModelResolver.updateForLiving(livingState.headItem, headEquipment, ItemDisplayContext.HEAD, false, livingEntity);
            }

            if (state instanceof HumanoidRenderState humanoidState) {
                humanoidState.headEquipment = shouldRenderHead ? headEquipment.copy() : ItemStack.EMPTY;
                ItemStack chestEquipment = replacementArmor.getBySlot(EquipmentSlot.CHEST);
                humanoidState.chestEquipment = HumanoidArmorLayer.shouldRender(chestEquipment, EquipmentSlot.CHEST) ? chestEquipment.copy() : ItemStack.EMPTY;
                ItemStack legsEquipment = replacementArmor.getBySlot(EquipmentSlot.LEGS);
                humanoidState.legsEquipment = HumanoidArmorLayer.shouldRender(legsEquipment, EquipmentSlot.LEGS) ? legsEquipment.copy() : ItemStack.EMPTY;
                ItemStack feetEquipment = replacementArmor.getBySlot(EquipmentSlot.FEET);
                humanoidState.feetEquipment = HumanoidArmorLayer.shouldRender(feetEquipment, EquipmentSlot.FEET) ? feetEquipment.copy() : ItemStack.EMPTY;
            }
        }
        return state;
    }
}
