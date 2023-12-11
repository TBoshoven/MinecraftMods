package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.CreatureMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifierClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class CreatureMagicMirrorBlockEntityModifier extends ItemBasedMagicMirrorBlockEntityModifier {
    /**
     * The entity type to use for the reflection.
     */
    private final EntityType<?> entityType;

    /**
     * The object that modifies the reflection in the mirror to show the replacement armor.
     */
    @Nullable
    private CreatureReflectionModifier reflectionModifier;

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
            if (ForgeRegistries.ENTITIES.containsKey(entityTypeKey)) {
                entityType = ForgeRegistries.ENTITIES.getValue(entityTypeKey);
            }
        }
        if (entityType == null || !CreatureMagicMirrorModifier.isSupportedEntityType(entityType)) {
            // Backward compatibility
            entityType = EntityType.SKELETON;
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
        ResourceLocation entityTypeKey = ForgeRegistries.ENTITIES.getKey(entityType);
        if (entityTypeKey != null) {
            nbt.putString("EntityType", entityTypeKey.toString());
        }
        return nbt;
    }

    @Override
    public void activate(MagicMirrorCoreBlockEntity blockEntity) {
        Reflection reflection = blockEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = createReflectionModifier();
            reflection.addModifier(reflectionModifier);
        }
    }

    private CreatureReflectionModifier createReflectionModifier() {
        return DistExecutor.runForDist(
                () -> () -> new CreatureReflectionModifierClient(entityType),
                () -> () -> new CreatureReflectionModifier(entityType)
        );
    }

    @Override
    public void deactivate(MagicMirrorCoreBlockEntity blockEntity) {
        if (reflectionModifier != null) {
            Reflection reflection = blockEntity.getReflection();
            if (reflection != null) {
                reflection.removeModifier(reflectionModifier);
            }
        }
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorCoreBlockEntity blockEntity, Player playerIn, InteractionHand hand) {
        // No behavior right now.
        return false;
    }
}
