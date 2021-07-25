package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorBlock;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class BlockStates extends BlockStateProvider {
    BlockStates(DataGenerator gen, ExistingFileHelper existingFileHelper) {
        super(gen, MagicMirrorMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile mirrorModel = new ModelFile.ExistingModelFile(modLoc("block/magic_mirror"), existingFileHelper);
        getVariantBuilder(Blocks.MAGIC_MIRROR.get())
                .forAllStates(state -> {
                            int rotation = state.getValue(MagicMirrorBlock.PART) == MagicMirrorBlock.EnumPartType.TOP ? 180 : 0;
                            return ConfiguredModel.builder()
                                    .modelFile(mirrorModel)
                                    .rotationX(rotation)
                                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + rotation) % 360)
                                    .build();
                        }
                );
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicMirrorMod.MOD_ID, super.getName());
    }
}
