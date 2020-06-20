package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BannerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerMagicMirrorTileEntityModifier extends MagicMirrorTileEntityModifier {
    private DyeColor baseColor = DyeColor.BLACK;
    @Nullable
    private CompoundNBT bannerNBT;
    @Nullable
    private ITextComponent name;

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
        ItemStack itemStack = new ItemStack(BannerBlock.forColor(baseColor));
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
        name = ITextComponent.Serializer.fromJson(nbt.getString("BannerName"));
    }

    @Override
    public void activate(MagicMirrorBaseTileEntity tileEntity) {

    }

    @Override
    public void deactivate(MagicMirrorBaseTileEntity tileEntity) {

    }

    @Override
    public boolean tryPlayerActivate(MagicMirrorBaseTileEntity tileEntity, PlayerEntity playerIn, Hand hand) {
        // No activation behavior
        return false;
    }
}
