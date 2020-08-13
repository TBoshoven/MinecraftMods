package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.google.common.collect.Lists;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.BannerReflectionModifier;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.BannerReflectionModifierClient;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerMagicMirrorTileEntityModifier extends MagicMirrorTileEntityModifier {
    /**
     * The initial color of the banner (before any patterns are applied)
     */
    private DyeColor baseColor = DyeColor.BLACK;
    /**
     * The banner NBT tag, stored here for removal.
     */
    @Nullable
    private CompoundNBT bannerNBT;
    /**
     * The banner name.
     */
    @Nullable
    private ITextComponent name;
    /**
     * The object that modifies the reflection in the mirror to show the banner in the background.
     */
    @Nullable
    private BannerReflectionModifier reflectionModifier;

    public BannerMagicMirrorTileEntityModifier(MagicMirrorModifier modifier) {
        super(modifier);
    }

    public BannerMagicMirrorTileEntityModifier(MagicMirrorModifier modifier, DyeColor baseColor, @Nullable CompoundNBT bannerNBT, @Nullable ITextComponent name) {
        super(modifier);
        this.baseColor = baseColor;
        this.bannerNBT = bannerNBT != null ? bannerNBT.copy() : null;
        this.name = name;
    }

    @Override
    public void remove(World world, BlockPos pos) {
        // BannerBlock.forColor is client-only.
        // Let's do a super-ugly workaround.
        // This will cause issues if dye colors are ever made extensible.
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(String.format("minecraft:%s_banner", baseColor.getTranslationKey())));
        ItemStack itemStack = new ItemStack(block);
        if (bannerNBT != null) {
            itemStack.getOrCreateTag().put("BlockEntityTag", bannerNBT);
        }
        if (name != null) {
            itemStack.setDisplayName(name);
        }
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        nbt.putInt("BannerColor", baseColor.getId());
        if (bannerNBT != null) {
            nbt.put("BannerData", bannerNBT.copy());
        }
        nbt.putString("BannerName", ITextComponent.Serializer.toJson(name));
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        baseColor = DyeColor.byId(nbt.getInt("BannerColor"));
        CompoundNBT bannerData = nbt.getCompound("BannerData");
        if (!bannerData.isEmpty()) {
            bannerNBT = bannerData.copy();
        }
        name = ITextComponent.Serializer.func_240643_a_(nbt.getString("BannerName"));
    }

    @Override
    public void activate(MagicMirrorBaseTileEntity tileEntity) {
        Reflection reflection = tileEntity.getReflection();
        if (reflection != null) {
            List<Pair<BannerPattern, DyeColor>> patternList = Lists.newArrayList();
            patternList.add(Pair.of(BannerPattern.BASE, baseColor));
            if (bannerNBT != null) {
                // Get the patterns from the NBT data
                ListNBT patterns = bannerNBT.getList("Patterns", 10);
                int size = patterns.size();
                for (int i = 0; i < size; ++i) {
                    CompoundNBT pattern = patterns.getCompound(i);
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

    private static BannerReflectionModifier createReflectionModifier(List<? extends Pair<BannerPattern, DyeColor>> patternList) {
        return DistExecutor.runForDist(
                () -> () -> new BannerReflectionModifierClient(patternList),
                () -> () -> new BannerReflectionModifier(patternList)
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
        // No activation behavior
        return false;
    }
}
