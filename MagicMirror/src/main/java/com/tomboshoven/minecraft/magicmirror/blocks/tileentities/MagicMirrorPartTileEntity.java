package com.tomboshoven.minecraft.magicmirror.blocks.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * The tile entity for the top mirror block; this tile entity has no reflection logic and simply uses whatever's in the
 * bottom block.
 */
public class MagicMirrorPartTileEntity extends MagicMirrorBaseTileEntity {
    @Nullable
    private MagicMirrorCoreTileEntity core;

    public MagicMirrorPartTileEntity() {
        super(Objects.requireNonNull(TileEntities.MAGIC_MIRROR_PART.get()));
    }

    @Nullable
    @Override
    public MagicMirrorCoreTileEntity getCore() {
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
