package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.BannerReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.BannerReflectionModifierClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class BannerMagicMirrorBlockEntityModifier extends ItemBasedMagicMirrorBlockEntityModifier {
    /**
     * The object that modifies the reflection in the mirror to show the banner in the background.
     */
    @Nullable
    private BannerReflectionModifier reflectionModifier;

    public BannerMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, ItemStack item) {
        super(modifier, item);
    }

    public BannerMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, CompoundTag nbt) {
        super(modifier, nbt);
    }

    @Override
    protected ItemStack getItemStackOldNbt(CompoundTag nbt) {
        DyeColor baseColor = DyeColor.byId(nbt.getInt("BannerColor"));
        CompoundTag bannerData = nbt.getCompound("BannerData");
        Component name = Component.Serializer.fromJson(nbt.getString("BannerName"));

        // BannerBlock.forColor is client-only.
        // Let's do a super-ugly workaround.
        // This will cause issues if dye colors are ever made extensible.
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(String.format("minecraft:%s_banner", baseColor.getName())));
        if (block == null) {
            return super.getItemStackOldNbt(nbt);
        }
        ItemStack itemStack = new ItemStack(block);
        itemStack.getOrCreateTag().put("BlockEntityTag", bannerData);
        if (name != null) {
            itemStack.setHoverName(name);
        }
        return itemStack;
    }

    @Override
    public void activate(MagicMirrorCoreBlockEntity blockEntity) {
        Reflection reflection = blockEntity.getReflection();
        if (reflection != null) {
            if (item.getItem() instanceof BannerItem bannerItem) {
                DyeColor baseColor = bannerItem.getColor();
                CompoundTag bannerNBT = BlockItem.getBlockEntityData(item);

                List<Pair<BannerPattern, DyeColor>> patternList = Lists.newArrayList();
                patternList.add(Pair.of(BannerPattern.BASE, baseColor));
                if (bannerNBT != null) {
                    // Get the patterns from the NBT data
                    ListTag patterns = bannerNBT.getList("Patterns", 10);
                    int size = patterns.size();
                    for (int i = 0; i < size; ++i) {
                        CompoundTag pattern = patterns.getCompound(i);
                        String patternHash = pattern.getString("Pattern");
                        Optional<BannerPattern> bannerPattern = Arrays.stream(BannerPattern.values()).filter(p -> p.getHashname().equals(patternHash)).findFirst();
                        if (bannerPattern.isPresent()) {
                            DyeColor bannerPatternColor = DyeColor.byId(pattern.getInt("Color"));
                            patternList.add(Pair.of(bannerPattern.get(), bannerPatternColor));
                        }
                    }
                }

                reflectionModifier = createReflectionModifier(patternList);

                reflection.addModifier(reflectionModifier);
            }
        }
    }

    private static BannerReflectionModifier createReflectionModifier(List<? extends Pair<BannerPattern, DyeColor>> patternList) {
        return DistExecutor.runForDist(
                () -> () -> new BannerReflectionModifierClient(patternList),
                () -> () -> new BannerReflectionModifier(patternList)
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
        // No activation behavior
        return false;
    }
}
