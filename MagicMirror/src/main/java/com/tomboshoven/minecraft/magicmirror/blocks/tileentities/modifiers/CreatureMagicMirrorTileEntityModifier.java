package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.CreatureReflectionModifierClient;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
        ResourceLocation entityTypeKey = new ResourceLocation(nbt.getString("EntityType"));
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityTypeKey);
        if (entityType == null) {
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

    private static CreatureReflectionModifier createReflectionModifier() {
        return DistExecutor.runForDist(
                () -> CreatureReflectionModifierClient::new,
                () -> CreatureReflectionModifier::new
        );
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
