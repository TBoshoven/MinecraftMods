package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The tile entity for the top mirror block; this tile entity has no reflection logic and simply uses whatever's in the
 * bottom block.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMirrorPartTileEntity extends MagicMirrorBaseTileEntity {
    @Nullable
    private MagicMirrorCoreTileEntity core;

    public MagicMirrorPartTileEntity() {
        super(TileEntities.MAGIC_MIRROR_PART.get());
    }

    @Nullable
    @Override
    protected MagicMirrorCoreTileEntity getCore() {
        if (core == null) {
            Level world = getLevel();
            if (world != null) {
                BlockEntity teBelow = world.getBlockEntity(getBlockPos().below());
                if (teBelow instanceof MagicMirrorCoreTileEntity) {
                    core = (MagicMirrorCoreTileEntity) teBelow;
                }
            }
        }
        return core;
    }
}
