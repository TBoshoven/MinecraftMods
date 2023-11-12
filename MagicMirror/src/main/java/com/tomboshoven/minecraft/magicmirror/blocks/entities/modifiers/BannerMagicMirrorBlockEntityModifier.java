package com.tomboshoven.minecraft.magicmirror.blocks.entities.modifiers;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.blocks.entities.MagicMirrorCoreBlockEntity;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.BannerReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.BannerReflectionModifierClient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.neoforged.fml.DistExecutor;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerMagicMirrorBlockEntityModifier extends MagicMirrorBlockEntityModifier {
    /**
     * The initial color of the banner (before any patterns are applied)
     */
    private DyeColor baseColor = DyeColor.BLACK;
    /**
     * The banner NBT tag, stored here for removal.
     */
    @Nullable
    private CompoundTag bannerNBT;
    /**
     * The banner name.
     */
    @Nullable
    private Component name;
    /**
     * The object that modifies the reflection in the mirror to show the banner in the background.
     */
    @Nullable
    private BannerReflectionModifier reflectionModifier;

    public BannerMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier) {
        super(modifier);
    }

    public BannerMagicMirrorBlockEntityModifier(MagicMirrorModifier modifier, DyeColor baseColor, @Nullable CompoundTag bannerNBT, @Nullable Component name) {
        super(modifier);
        this.baseColor = baseColor;
        this.bannerNBT = bannerNBT != null ? bannerNBT.copy() : null;
        this.name = name;
    }

    @Override
    public void remove(Level world, BlockPos pos) {
        // BannerBlock.forColor is client-only.
        // Let's do a super-ugly workaround.
        // This will cause issues if dye colors are ever made extensible.
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(String.format("minecraft:%s_banner", baseColor.getName())));
        ItemStack itemStack = new ItemStack(block);
        if (bannerNBT != null) {
            itemStack.getOrCreateTag().put("BlockEntityTag", bannerNBT);
        }
        if (name != null) {
            itemStack.setHoverName(name);
        }
        Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        super.write(nbt);
        nbt.putInt("BannerColor", baseColor.getId());
        if (bannerNBT != null) {
            nbt.put("BannerData", bannerNBT.copy());
        }
        nbt.putString("BannerName", Component.Serializer.toJson(name));
        return nbt;
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);
        baseColor = DyeColor.byId(nbt.getInt("BannerColor"));
        CompoundTag bannerData = nbt.getCompound("BannerData");
        if (!bannerData.isEmpty()) {
            bannerNBT = bannerData.copy();
        }
        name = Component.Serializer.fromJson(nbt.getString("BannerName"));
    }

    @Override
    public void activate(MagicMirrorCoreBlockEntity blockEntity) {
        Reflection reflection = blockEntity.getReflection();
        if (reflection != null) {
            List<Pair<Holder<BannerPattern>, DyeColor>> patternList = Lists.newArrayList();
            Optional<? extends Holder<BannerPattern>> base = BuiltInRegistries.BANNER_PATTERN.getHolder(BannerPatterns.BASE);
            base.ifPresent(bannerPatternHolder -> patternList.add(Pair.of(bannerPatternHolder, baseColor)));
            if (bannerNBT != null) {
                // Get the patterns from the NBT data
                ListTag patterns = bannerNBT.getList("Patterns", 10);
                int size = patterns.size();
                for (int i = 0; i < size; ++i) {
                    CompoundTag pattern = patterns.getCompound(i);
                    String patternHash = pattern.getString("Pattern");
                    Holder<BannerPattern> bannerPattern = BannerPattern.byHash(patternHash);
                    if (bannerPattern != null) {
                        DyeColor bannerPatternColor = DyeColor.byId(pattern.getInt("Color"));
                        patternList.add(Pair.of(bannerPattern, bannerPatternColor));
                    }
                }
            }

            reflectionModifier = createReflectionModifier(patternList);

            reflection.addModifier(reflectionModifier);
        }
    }

    private static BannerReflectionModifier createReflectionModifier(List<? extends Pair<Holder<BannerPattern>, DyeColor>> patternList) {
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
