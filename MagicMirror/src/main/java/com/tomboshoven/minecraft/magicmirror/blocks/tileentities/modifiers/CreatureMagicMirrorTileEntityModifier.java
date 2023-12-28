package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.CreatureMagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifierClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class CreatureMagicMirrorTileEntityModifier extends ItemBasedMagicMirrorTileEntityModifier {
    /**
     * The entity type to use for the reflection.
     */
    private final EntityType<?> entityType;

    /**
     * The object that modifies the reflection in the mirror to show the replacement armor.
     */
    @Nullable
    private CreatureReflectionModifier reflectionModifier;

    public CreatureMagicMirrorTileEntityModifier(MagicMirrorModifier modifier, ItemStack item, EntityType<?> entityType) {
        super(modifier, item);
        this.entityType = entityType;
    }

    public CreatureMagicMirrorTileEntityModifier(MagicMirrorModifier modifier, CompoundNBT nbt) {
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
    protected ItemStack getItemStackOldNbt(CompoundNBT nbt) {
        return new ItemStack(Items.SKELETON_SKULL);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        ResourceLocation entityTypeKey = ForgeRegistries.ENTITIES.getKey(entityType);
        if (entityTypeKey != null) {
            nbt.putString("EntityType", entityTypeKey.toString());
        }
        return nbt;
    }

    @Override
    public void activate(MagicMirrorBaseTileEntity tileEntity) {
        Reflection reflection = tileEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = createReflectionModifier();
            reflection.addModifier(reflectionModifier);
        }
    }

    private CreatureReflectionModifier createReflectionModifier() {
        return FMLEnvironment.dist == Dist.CLIENT ? new CreatureReflectionModifierClient() : new CreatureReflectionModifier();
    }

    @Override
    public void deactivate(MagicMirrorBaseTileEntity tileEntity) {
        if (reflectionModifier != null) {
            Reflection reflection = tileEntity.getReflection();
            if (reflection != null) {
                reflection.removeModifier(reflectionModifier);
            }
        }
    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorBaseTileEntity tileEntity, PlayerEntity playerIn, Hand hand) {
        // No behavior right now.
        return false;
    }
}
