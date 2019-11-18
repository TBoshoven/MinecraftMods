package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

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

public class TileEntityMagicDoor extends TileEntity {
    IBlockState textureBlock = Blocks.AIR.getDefaultState();

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return writeInternal(super.writeToNBT(compound));
    }

    public NBTTagCompound writeInternal(NBTTagCompound compound) {
        NBTTagCompound result = super.writeToNBT(compound);
        ResourceLocation registryName = this.textureBlock.getBlock().getRegistryName();
        if (registryName != null) {
            result.setString("textureBlock", registryName.toString());
            result.setShort("textureBlockData", (short) textureBlock.getBlock().getMetaFromState(textureBlock));
        }
        return result;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        String registryName = compound.getString("textureBlock");
        Block block = Block.getBlockFromName(registryName);
        if (block != null) {
            short blockMeta = compound.getShort("textureBlockData");
            textureBlock = block.getStateFromMeta(blockMeta);
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

    public IBlockState getTextureBlock() {
        return textureBlock;
    }

    public void setTextureBlock(IBlockState textureBlock) {
        this.textureBlock = textureBlock;
    }
}
