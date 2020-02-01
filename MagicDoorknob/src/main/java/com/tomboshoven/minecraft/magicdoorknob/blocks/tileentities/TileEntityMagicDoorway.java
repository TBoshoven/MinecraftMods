package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

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
public class TileEntityMagicDoorway extends TileEntity {
    private IBlockState replacedBlock = Blocks.AIR.getDefaultState();

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return writeInternal(super.writeToNBT(compound));
    }

    private NBTTagCompound writeInternal(NBTTagCompound compound) {
        NBTTagCompound result = super.writeToNBT(compound);
        ResourceLocation registryName = replacedBlock.getBlock().getRegistryName();
        if (registryName != null) {
            result.setString("replacedBlock", registryName.toString());
            result.setShort("replacedBlockData", (short)replacedBlock.getBlock().getMetaFromState(replacedBlock));
        }
        return result;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        String registryName = compound.getString("replacedBlock");
        Block block = Block.getBlockFromName(registryName);
        if (block != null) {
            short blockMeta = compound.getShort("replacedBlockData");
            replacedBlock = block.getStateFromMeta(blockMeta);
        }
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

    public IBlockState getReplacedBlock() {
        return replacedBlock;
    }

    public void setReplacedBlock(IBlockState replacedBlock) {
        this.replacedBlock = replacedBlock;
    }
}
