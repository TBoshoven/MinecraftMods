package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.NameTagReflectionModifier;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.tomboshoven.minecraft.magicmirror.MagicMirrorMod.LOGGER;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NameTagMagicMirrorBlockEntityModifier extends MagicMirrorBlockEntityModifier {
    @Nullable
    Component nameTag;

    /**
     * The object that modifies the tag shown in the reflection.
     */
    @Nullable
    private NameTagReflectionModifier reflectionModifier;

    /**
     * @param modifier The modifier that applied this object to the block entity.
     */
    public NameTagMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier) {
        super(modifier);
    }

    public NameTagMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, Component nameTag) {
        this(modifier);
        this.nameTag = nameTag;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        CompoundTag tag = super.write(nbt);
        if (nameTag != null) {
            tag.putString("NameTag", Component.Serializer.toJson(nameTag));
        }
        return tag;
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);

        if (nbt.contains("NameTag", 8)) {
            String name = nbt.getString("NameTag");
            try {
                nameTag = Component.Serializer.fromJson(name);
            } catch (Exception exception) {
                LOGGER.warn("Failed to parse item tag {}", name, exception);
            }
        }
    }

    @Override
    public void remove(Level world, BlockPos pos) {
        ItemStack itemStack = new ItemStack(Items.NAME_TAG);
        itemStack.setHoverName(nameTag);
        Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    @Override
    public void activate(MagicMirrorCoreBlockEntity blockEntity) {
        Reflection reflection = blockEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = createReflectionModifier();
            reflection.addModifier(reflectionModifier);
        }
    }

    private NameTagReflectionModifier createReflectionModifier() {
        return new NameTagReflectionModifier(nameTag);
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
        // No behavior.
        return false;
    }
}
