package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Block model definition that can be used to provide textures to its provided models.
 * This adds texturing functionality on top of an existing block model definition.
 *
 * @param baseModelDefinition The model definition to wrap.
 */
public record TexturedBlockModelDefinition(
        BlockModelDefinition baseModelDefinition) implements CustomBlockModelDefinition {
    /**
     * Codec for the textured block model definition.
     */
    public static final MapCodec<TexturedBlockModelDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(BlockModelDefinition.VANILLA_CODEC.forGetter(TexturedBlockModelDefinition::baseModelDefinition)).apply(instance, TexturedBlockModelDefinition::new));

    /**
     * @return A new instance of the desired texture mapper.
     */
    private static TextureMapper createTextureMapper() {
        // Only one texture mapper is supported for now.
        return new ModelDataTextureMapper();
    }

    @Override
    public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(StateDefinition<Block, BlockState> states, Supplier<String> sourceSupplier) {
        TextureMapper textureMapper = createTextureMapper();
        Map<BlockState, BlockStateModel.UnbakedRoot> instantiatedBase = baseModelDefinition.instantiate(states, sourceSupplier);
        // Wrap the results
        for (Map.Entry<BlockState, BlockStateModel.UnbakedRoot> entry : instantiatedBase.entrySet()) {
            entry.setValue(wrapTextured(entry.getValue(), textureMapper));
        }
        return instantiatedBase;
    }

    @Override
    public MapCodec<? extends CustomBlockModelDefinition> codec() {
        return CODEC;
    }

    /**
     * Wrap the unbaked block state model in a version that bakes into a textured block state model.
     *
     * @param base          The base unbaked block state model to wrap.
     * @param textureMapper The texture mapper to use.
     * @return The textured version of the unbaked block state model.
     */
    private static BlockStateModel.UnbakedRoot wrapTextured(BlockStateModel.UnbakedRoot base, TextureMapper textureMapper) {
        return new BlockStateModel.UnbakedRoot() {
            @Override
            public BlockStateModel bake(BlockState state, ModelBaker baker) {
                return new TexturedBlockStateModel(base.bake(state, baker), textureMapper, baker.sprites());
            }

            @Override
            public Object visualEqualityGroup(BlockState state) {
                return base.visualEqualityGroup(state);
            }

            @Override
            public void resolveDependencies(Resolver resolver) {
                base.resolveDependencies(resolver);
            }
        };
    }
}
