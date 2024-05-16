package com.tomboshoven.minecraft.magicmirror.blocks.entities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * The tile entity for the top mirror block; this tile entity has no reflection logic and simply uses whatever's in the
 * bottom block.
 */
public class MagicMirrorPartBlockEntity extends MagicMirrorBaseBlockEntity {
    @Nullable
    private MagicMirrorCoreBlockEntity core;

    public MagicMirrorPartBlockEntity() {
        super(Objects.requireNonNull(BlockEntities.MAGIC_MIRROR_PART.get()));
    }

    @Nullable
    @Override
    public MagicMirrorCoreBlockEntity getCore() {
        if (core == null) {
            World world = getLevel();
            if (world != null) {
                TileEntity teBelow = world.getBlockEntity(getBlockPos().below());
                if (teBelow instanceof MagicMirrorCoreBlockEntity) {
                    core = (MagicMirrorCoreBlockEntity) teBelow;
                }
            }
        }
        return core;
    }
}
