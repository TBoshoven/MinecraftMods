package com.tomboshoven.minecraft.magicdoorknob.blocks.tileentities;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * Tile entity for the magic door parts.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicDoorTileEntity extends MagicDoorwayPartBaseTileEntity {
    private UUID id = MathHelper.getRandomUUID();
    private UUID pairedDoorId;

    public MagicDoorTileEntity() {
        super(TileEntities.MAGIC_DOOR.get());
    }

    public void pairWith(@Nullable MagicDoorTileEntity otherDoor) {
        if (otherDoor == null) {
            pairedDoorId = null;
        }
        else {
            pairedDoorId = otherDoor.id;
            otherDoor.pairedDoorId = id;
        }
    }

    public boolean isPairedWith(MagicDoorTileEntity otherDoor) {
        return id == otherDoor.id;
    }

    public void setDoorknob(ItemStack doorknobStack) {
        doorknob = doorknobStack;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return writeInternal(compound);
    }

    private CompoundNBT writeInternal(CompoundNBT compound) {
        CompoundNBT result = super.write(compound);
        result.putUniqueId("id", id);
        if (pairedDoorId != null) {
            result.putUniqueId("pairedDoorId", pairedDoorId);
        }
        if (doorknob != null) {
            CompoundNBT doorknobStackCompound = doorknob.write(new CompoundNBT());
            result.put("doorknobStack", doorknobStackCompound);
        }
        return result;
    }

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT compound) {
        super.func_230337_a_(state, compound);
        read(compound);
    }

    private void read(CompoundNBT compound) {
        if (compound.hasUniqueId("id")) {
            id = compound.getUniqueId("id");
        }
        if (compound.hasUniqueId("pairedDoorId")) {
            pairedDoorId = compound.getUniqueId("pairedDoorId");
        }
        CompoundNBT doorknobStackCompound = compound.getCompound("doorknobStack");
        doorknob = ItemStack.read(doorknobStackCompound);
    }
}
