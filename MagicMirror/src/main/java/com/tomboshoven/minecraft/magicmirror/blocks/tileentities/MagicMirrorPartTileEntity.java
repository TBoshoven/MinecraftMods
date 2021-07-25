package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
            World world = getLevel();
            if (world != null) {
                TileEntity teBelow = world.getBlockEntity(getBlockPos().below());
                if (teBelow instanceof MagicMirrorCoreTileEntity) {
                    core = (MagicMirrorCoreTileEntity) teBelow;
                }
            }
        }
        return core;
    }
}
