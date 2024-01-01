package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

class BlockStates extends BlockStateProvider {
    BlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MagicDoorknobMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerDoor();
        registerDoorway();
    }

    private void registerDoor() {
        ModelFile doorBottom = models().getExistingFile(modLoc("block/magic_door_bottom"));
        ModelFile doorTop = models().getExistingFile(modLoc("block/magic_door_top"));
        horizontalBlock(Blocks.MAGIC_DOOR.get(), blockState -> blockState.getValue(MagicDoorBlock.PART) == MagicDoorwayPartBaseBlock.EnumPartType.TOP ? doorTop : doorBottom, 270);
    }

    private void registerDoorway() {
        ModelFile doorwayClosedBottom = models().getExistingFile(modLoc("block/magic_doorway_closed_bottom"));
        ModelFile doorwayClosedTop = models().getExistingFile(modLoc("block/magic_doorway_closed_top"));
        ModelFile doorwayHalfOpenBottom = models().getExistingFile(modLoc("block/magic_doorway_half_open_bottom"));
        ModelFile doorwayHalfOpenTop = models().getExistingFile(modLoc("block/magic_doorway_half_open_top"));
        ModelFile doorwayOpenBottom = models().getExistingFile(modLoc("block/magic_doorway_open_bottom"));
        ModelFile doorwayOpenTop = models().getExistingFile(modLoc("block/magic_doorway_open_top"));

        getVariantBuilder(Blocks.MAGIC_DOORWAY.get()).forAllStates(
                blockState -> {
                    boolean openEastWest = blockState.getValue(MagicDoorwayBlock.OPEN_EAST_WEST);
                    boolean openNorthSouth = blockState.getValue(MagicDoorwayBlock.OPEN_NORTH_SOUTH);
                    MagicDoorwayPartBaseBlock.EnumPartType part = blockState.getValue(MagicDoorwayBlock.PART);
                    ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
                    if (openEastWest) {
                        if (openNorthSouth) {
                            builder = builder.modelFile(part == MagicDoorwayPartBaseBlock.EnumPartType.TOP ? doorwayOpenTop : doorwayOpenBottom);
                        } else {
                            builder = builder.modelFile(part == MagicDoorwayPartBaseBlock.EnumPartType.TOP ? doorwayHalfOpenTop : doorwayHalfOpenBottom);
                        }
                    } else {
                        if (openNorthSouth) {
                            builder = builder.modelFile(part == MagicDoorwayPartBaseBlock.EnumPartType.TOP ? doorwayHalfOpenTop : doorwayHalfOpenBottom).rotationY(90);
                        } else {
                            builder = builder.modelFile(part == MagicDoorwayPartBaseBlock.EnumPartType.TOP ? doorwayClosedTop : doorwayClosedBottom);
                        }
                    }
                    return builder.build();
                }
        );
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicDoorknobMod.MOD_ID, super.getName());
    }
}
