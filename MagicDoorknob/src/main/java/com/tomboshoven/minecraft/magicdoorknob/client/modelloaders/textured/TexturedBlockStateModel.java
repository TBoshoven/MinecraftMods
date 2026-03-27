package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.MaterialInfoSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Block state model that provides dynamic texturing functionality to textured models.
 */
public class TexturedBlockStateModel extends DelegateBlockStateModel {
    /**
     * Texture mapper for the block state model.
     */
    private final TextureMapper textureMapper;
    /**
     * The material baker to use for obtaining baked materials from unbaked ones.
     */
    private final MaterialBaker materialBaker;
    /**
     * Cache for collectParts, which runs fairly frequently on the same inputs and ideally doesn't do a lot of computation.
     * This uses the mapping key (see {@link TextureMapper.BlockStateTextureMapper#getMappingKey()}) as a unique key.
     */
    private final ConcurrentHashMap<@Nullable Object, List<BlockStateModelPart>> partCache = new ConcurrentHashMap<>();

    /**
     * @param baseModel     The block state model to enhance with texturing functionality.
     * @param textureMapper The texture mapper.
     * @param materialBaker The material baker to use for obtaining baked materials from unbaked ones.
     */
    public TexturedBlockStateModel(BlockStateModel baseModel, TextureMapper textureMapper, MaterialBaker materialBaker) {
        super(baseModel);
        this.textureMapper = textureMapper;
        this.materialBaker = materialBaker;
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        Object baseKey = delegate.createGeometryKey(level, pos, state, random);
        if (baseKey == null) {
            return null;
        }
        ModelData modelData = level.getModelData(pos);
        Object mappingKey = textureMapper.forBlockState(state, modelData).getMappingKey();
        if (mappingKey == null) {
            return null;
        }
        return new GeometryKey(baseKey, mappingKey);
    }

    /**
     * Simple record to provide key matching functionality.
     */
    private record GeometryKey(Object baseKey, Object mappingKey) {
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        TextureMapper.BlockStateTextureMapper mapper = textureMapper.forBlockState(state, level.getModelData(pos));
        List<BlockStateModelPart> delegatePartList = partCache.computeIfAbsent(mapper.getMappingKey(), key -> {
            List<BlockStateModelPart> partList = new ArrayList<>();
            delegate.collectParts(level, pos, state, random, partList);
            partList.replaceAll(part -> wrapPart(part, mapper, random));
            return partList;
        });
        parts.addAll(delegatePartList);
    }

    @Override
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        Material.Baked baseParticleMaterial = delegate.particleMaterial(level, pos, state);
        if (baseParticleMaterial.sprite() instanceof PropertySprite baseIconProperty) {
            ModelData modelData = level.getModelData(pos);
            MaterialInfoSource materialInfoSource = textureMapper.forBlockState(state, modelData).mapSprite(baseIconProperty);
            if (materialInfoSource != null) {
                return materialInfoSource.lookupMaterial(materialBaker);
            }
        }
        return baseParticleMaterial;
    }

    /**
     * Wrap a block state model part with its textured version.
     *
     * @param basePart The part to wrap.
     * @param mapper   The texture mapper to use.
     * @param random   The random source for the part collection.
     * @return The wrapped block model part.
     */
    private BlockStateModelPart wrapPart(BlockStateModelPart basePart, TextureMapper.BlockStateTextureMapper mapper, RandomSource random) {
        return new TexturedStateBlockModelPart(basePart, mapper, materialBaker, random);
    }
}
