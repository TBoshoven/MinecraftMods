package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import com.tomboshoven.minecraft.magicdoorknob.items.ItemMagicDoorknob;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityMagicDoorwayPartBase extends TileEntity {
    private IBlockState baseBlockState = Blocks.AIR.getDefaultState();
    private ItemMagicDoorknob doorknob;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return writeInternal(super.writeToNBT(compound));
    }

    private NBTTagCompound writeInternal(NBTTagCompound compound) {
        NBTTagCompound result = super.writeToNBT(compound);
        ResourceLocation registryName = baseBlockState.getBlock().getRegistryName();
        if (registryName != null) {
            result.setString("baseBlock", registryName.toString());
            result.setShort("baseBlockData", (short) baseBlockState.getBlock().getMetaFromState(baseBlockState));
        }
        if (doorknob != null) {
            result.setString("doorknobType", doorknob.getTypeName());
        }
        return result;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        String registryName = compound.getString("baseBlock");
        Block block = Block.getBlockFromName(registryName);
        if (block != null) {
            short blockMeta = compound.getShort("baseBlockData");
            baseBlockState = block.getStateFromMeta(blockMeta);
        }
        String doorknobType = compound.getString("doorknobType");
        doorknob = Items.itemDoorknobs.get(doorknobType);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeInternal(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public IBlockState getBaseBlockState() {
        return baseBlockState;
    }

    public void setBaseBlockState(IBlockState baseBlockState) {
        this.baseBlockState = baseBlockState;
    }

    @Nullable
    public ItemMagicDoorknob getDoorknob() {
        return doorknob;
    }

    public void setDoorknob(ItemMagicDoorknob doorknob) {
        this.doorknob = doorknob;
    }
}
