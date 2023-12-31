package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.CreatureMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CreatureMagicMirrorBlockEntityModifier extends ItemBasedMagicMirrorBlockEntityModifier {
    /**
     * The entity type to use for the reflection.
     */
    private final EntityType<?> entityType;

    public CreatureMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item, EntityType<?> entityType) {
        super(modifier, item);
        this.entityType = entityType;
    }

    public CreatureMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt) {
        super(modifier, nbt);
        EntityType<?> entityType = null;
        if (nbt.contains("EntityType", 8)) {
            ResourceLocation entityTypeKey = new ResourceLocation(nbt.getString("EntityType"));
            // Extra check to make sure we're not getting the default
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(entityTypeKey)) {
                entityType = BuiltInRegistries.ENTITY_TYPE.get(entityTypeKey);
            }
        }
        if (entityType == null || !CreatureMagicMirrorModifier.isSupportedEntityType(entityType)) {
            // Backward compatibility
            entityType = CreatureMagicMirrorModifier.getDefaultEntityType();
        }
        this.entityType = entityType;
    }

    @Override
    protected ItemStack getItemStackOldNbt(CompoundTag nbt) {
        return new ItemStack(Items.SKELETON_SKULL);
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        super.write(nbt);
        ResourceLocation entityTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        nbt.putString("EntityType", entityTypeKey.toString());
        return nbt;
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorCoreBlockEntity blockEntity, Player playerIn, InteractionHand hand) {
        // No behavior right now.
        return false;
    }

    /**
     * @return the entity type to change the reflection to.
     */
    public EntityType<?> getEntityType() {
        return entityType;
    }
}
