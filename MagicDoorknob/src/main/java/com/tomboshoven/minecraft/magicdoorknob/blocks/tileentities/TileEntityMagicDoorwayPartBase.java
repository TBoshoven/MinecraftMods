package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import com.tomboshoven.minecraft.magicdoorknob.items.ItemMagicDoorknob;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Base class for tile entities that make up magic doorways.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityMagicDoorwayPartBase extends TileEntity {
    // The block we're basing the appearance of this block on.
    private BlockState baseBlockState = Blocks.AIR.getDefaultState();
    // The doorknob that caused this block to be created.
    private ItemMagicDoorknob doorknob;

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return writeInternal(compound);
    }

    private CompoundNBT writeInternal(CompoundNBT compound) {
        CompoundNBT result = super.writeToNBT(compound);
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
    public void readFromNBT(CompoundNBT compound) {
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
    public CompoundNBT getUpdateTag() {
        return writeInternal(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    /**
     * @return The blockstate that the appearance of this block is based on.
     */
    public BlockState getBaseBlockState() {
        return baseBlockState;
    }

    /**
     * @param baseBlockState The blockstate that the appearance of this block is based on.
     */
    public void setBaseBlockState(BlockState baseBlockState) {
        this.baseBlockState = baseBlockState;
    }

    /**
     * @return The doorknob that was used to create this block.
     */
    @Nullable
    public ItemMagicDoorknob getDoorknob() {
        return doorknob;
    }

    /**
     * @param doorknob The doorknob that was used to create this block.
     */
    public void setDoorknob(ItemMagicDoorknob doorknob) {
        this.doorknob = doorknob;
    }
}
