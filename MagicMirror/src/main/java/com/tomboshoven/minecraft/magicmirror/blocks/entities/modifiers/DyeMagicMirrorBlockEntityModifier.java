package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.DyeReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.DyeReflectionModifierClient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DyeMagicMirrorBlockEntityModifier extends MagicMirrorBlockEntityModifier {
    /**
     * The dye to use for the reflection.
     */
    @Nullable
    private DyeItem dye = null;

    /**
     * The object that modifies the reflection in the mirror to change the color.
     */
    @Nullable
    private DyeReflectionModifier reflectionModifier;

    /**
     * @param modifier The modifier that applied this object to the block entity.
     */
    public DyeMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier) {
        super(modifier);
    }

    /**
     * @param modifier The modifier that applied this object to the block entity.
     * @param dye      The dye to use for determining the color.
     */
    public DyeMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, DyeItem dye) {
        this(modifier);
        this.dye = dye;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        super.write(nbt);
        if (dye != null) {
            ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(dye);
            if (itemKey != null) {
                nbt.putString("DyeItem", itemKey.toString());
            }
        }
        return nbt;
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);
        if (nbt.contains("DyeItem", 8)) {
            ResourceLocation dyeItemKey = new ResourceLocation(nbt.getString("DyeItem"));
            Item item = ForgeRegistries.ITEMS.getValue(dyeItemKey);
            if (item instanceof DyeItem) {
                this.dye = (DyeItem) item;
            }
        }
    }

    @Override
    public void remove(Level world, BlockPos pos) {
        if (dye != null) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(dye, 1));
        }
    }

    @Override
    public void activate(MagicMirrorCoreBlockEntity blockEntity) {
        Reflection reflection = blockEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = createReflectionModifier();
            reflection.addModifier(reflectionModifier);
        }
    }

    private DyeReflectionModifier createReflectionModifier() {
        float[] color;
        if (dye == null) {
            color = new float[] { 1f, 1f, 1f, 1f };
        }
        else {
            float[] textureDiffuseColors = dye.getDyeColor().getTextureDiffuseColors();
            color = new float[] { textureDiffuseColors[0], textureDiffuseColors[1], textureDiffuseColors[2], 1f };
        }
        return DistExecutor.runForDist(
                () -> () -> new DyeReflectionModifierClient(color),
                () -> () -> new DyeReflectionModifier(color)
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
