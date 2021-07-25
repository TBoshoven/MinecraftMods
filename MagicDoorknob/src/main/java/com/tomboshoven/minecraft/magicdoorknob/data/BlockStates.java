package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock.EnumPartType.TOP;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class BlockStates extends BlockStateProvider {
    BlockStates(DataGenerator gen, ExistingFileHelper existingFileHelper) {
        super(gen, MagicDoorknobMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerDoor();
        registerDoorway();
    }

    private void registerDoor() {
        ModelFile doorBottom = new ModelFile.ExistingModelFile(modLoc("block/magic_door_bottom"), existingFileHelper);
        ModelFile doorTop = new ModelFile.ExistingModelFile(modLoc("block/magic_door_top"), existingFileHelper);
        horizontalBlock(Blocks.MAGIC_DOOR.get(), blockState -> blockState.getValue(MagicDoorBlock.PART) == TOP ? doorTop : doorBottom, 270);
    }

    private void registerDoorway() {
        ModelFile doorwayClosedBottom = new ModelFile.ExistingModelFile(modLoc("block/magic_doorway_closed_bottom"), existingFileHelper);
        ModelFile doorwayClosedTop = new ModelFile.ExistingModelFile(modLoc("block/magic_doorway_closed_top"), existingFileHelper);
        ModelFile doorwayHalfOpenBottom = new ModelFile.ExistingModelFile(modLoc("block/magic_doorway_half_open_bottom"), existingFileHelper);
        ModelFile doorwayHalfOpenTop = new ModelFile.ExistingModelFile(modLoc("block/magic_doorway_half_open_top"), existingFileHelper);
        ModelFile doorwayOpenBottom = new ModelFile.ExistingModelFile(modLoc("block/magic_doorway_open_bottom"), existingFileHelper);
        ModelFile doorwayOpenTop = new ModelFile.ExistingModelFile(modLoc("block/magic_doorway_open_top"), existingFileHelper);

        getVariantBuilder(Blocks.MAGIC_DOORWAY.get()).forAllStates(
                blockState -> {
                    boolean openEastWest = blockState.getValue(MagicDoorwayBlock.OPEN_EAST_WEST);
                    boolean openNorthSouth = blockState.getValue(MagicDoorwayBlock.OPEN_NORTH_SOUTH);
                    MagicDoorwayPartBaseBlock.EnumPartType part = blockState.getValue(MagicDoorwayBlock.PART);
                    ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
                    if (openEastWest) {
                        if (openNorthSouth) {
                            builder = builder.modelFile(part == TOP ? doorwayOpenTop : doorwayOpenBottom);
                        }
                        else {
                            builder = builder.modelFile(part == TOP ? doorwayHalfOpenTop : doorwayHalfOpenBottom);
                        }
                    }
                    else {
                        if (openNorthSouth) {
                            builder = builder.modelFile(part == TOP ? doorwayHalfOpenTop : doorwayHalfOpenBottom).rotationY(90);
                        }
                        else {
                            builder = builder.modelFile(part == TOP ? doorwayClosedTop : doorwayClosedBottom);
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
