package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.MagicMirrorBaseTileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BannerMagicMirrorTileEntityModifier extends MagicMirrorTileEntityModifier {
    public BannerMagicMirrorTileEntityModifier(MagicMirrorModifier modifier) {
        super(modifier);
    }

    @Override
    public void remove(World world, BlockPos pos) {

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
