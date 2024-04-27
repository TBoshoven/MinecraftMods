package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.CreatureMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
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

    public CreatureMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt, HolderLookup.Provider holderLookupProvider) {
        super(modifier, nbt, holderLookupProvider);
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
    protected ItemStack getItemStackOldNbt(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        return new ItemStack(Items.SKELETON_SKULL);
    }

    @Override
    public CompoundTag write(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        super.write(nbt, lookupProvider);
        ResourceLocation entityTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        nbt.putString("EntityType", entityTypeKey.toString());
        return nbt;
    }

    /**
     * @return the entity type to change the reflection to.
     */
    public EntityType<?> getEntityType() {
        return entityType;
    }
}
