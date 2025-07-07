package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.CreatureMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

public class CreatureMagicMirrorBlockEntityModifier extends ItemBasedMagicMirrorBlockEntityModifier {
    /**
     * The entity type to use for the reflection.
     */
    private final EntityType<?> entityType;

    public CreatureMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item, EntityType<?> entityType) {
        super(modifier, item);
        this.entityType = entityType;
    }

    public CreatureMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ValueInput input) {
        super(modifier, input);
        Optional<? extends EntityType<?>> foundEntityType = input.getString("EntityType")
                .map(ResourceLocation::parse)
                // Extra check to make sure we're not getting the default
                .filter(BuiltInRegistries.ENTITY_TYPE::containsKey)
                .map(BuiltInRegistries.ENTITY_TYPE::getValue)
                .filter(CreatureMagicMirrorModifier::isSupportedEntityType);
        // Can't use orElse because EntityType type parameter may not match; limitation of Optional
        if (foundEntityType.isEmpty()) {
            // Backward compatibility
            this.entityType = CreatureMagicMirrorModifier.getDefaultEntityType();
        } else {
            this.entityType = foundEntityType.get();
        }
    }

    @Override
    public void save(ValueOutput output) {
        super.save(output);
        ResourceLocation entityTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        output.putString("EntityType", entityTypeKey.toString());
    }

    /**
     * @return The entity type to change the reflection to.
     */
    public EntityType<?> getEntityType() {
        return entityType;
    }
}
