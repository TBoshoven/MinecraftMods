package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorInactiveBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

class BlockStates extends BlockStateProvider {
    BlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MagicMirrorMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile mirrorModel = models().getExistingFile(modLoc("block/magic_mirror"));
        getVariantBuilder(Blocks.MAGIC_MIRROR_INACTIVE.get())
                .forAllStates(state -> {
                            int rotation = state.getValue(MagicMirrorInactiveBlock.PART) == MagicMirrorInactiveBlock.EnumPartType.TOP ? 180 : 0;
                            return ConfiguredModel.builder()
                                    .modelFile(mirrorModel)
                                    .rotationX(rotation)
                                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + rotation) % 360)
                                    .build();
                        }
                );
        getVariantBuilder(Blocks.MAGIC_MIRROR_CORE.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(mirrorModel)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()) % 360)
                        .build()
                );
        getVariantBuilder(Blocks.MAGIC_MIRROR_PART.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(mirrorModel)
                        .rotationX(180)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build()
                );
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicMirrorMod.MOD_ID, super.getName());
    }
}
