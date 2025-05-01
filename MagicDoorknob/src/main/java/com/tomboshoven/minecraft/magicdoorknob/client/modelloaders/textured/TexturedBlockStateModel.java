package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
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
     * The sprite getter to use for looking up the textures.
     */
    private final SpriteGetter sprites;
    /**
     * Cache for collectParts, which runs fairly frequently on the same inputs and ideally doesn't do a lot of computation.
     * This uses the mapping key (see {@link TextureMapper.BlockStateTextureMapper#getMappingKey()}) as a unique key.
     */
    private final ConcurrentHashMap<Object, List<BlockModelPart>> partCache = new ConcurrentHashMap<>();

    /**
     * @param baseModel     The block state model to enhance with texturing functionality.
     * @param textureMapper The texture mapper.
     * @param sprites       The sprite getter to performing texture lookups.
     */
    public TexturedBlockStateModel(BlockStateModel baseModel, TextureMapper textureMapper, SpriteGetter sprites) {
        super(baseModel);
        this.textureMapper = textureMapper;
        this.sprites = sprites;
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
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
        TextureMapper.BlockStateTextureMapper mapper = textureMapper.forBlockState(state, level.getModelData(pos));
        List<BlockModelPart> delegatePartList = partCache.computeIfAbsent(mapper.getMappingKey(), key -> {
            List<BlockModelPart> partList = new ArrayList<>();
            delegate.collectParts(level, pos, state, random, partList);
            partList.replaceAll(part -> wrapPart(part, mapper));
            return partList;
        });
        parts.addAll(delegatePartList);
    }

    @Override
    public TextureAtlasSprite particleIcon(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        TextureAtlasSprite baseIcon = delegate.particleIcon(level, pos, state);
        if (baseIcon instanceof PropertySprite baseIconProperty) {
            ModelData modelData = level.getModelData(pos);
            Material material = textureMapper.forBlockState(state, modelData).mapSprite(baseIconProperty);
            if (material == null) {
                return baseIcon;
            }
            return sprites.get(material, () -> "TexturedBlockStateModel");
        }
        return baseIcon;
    }

    /**
     * Wrap a block model part with its textured version.
     *
     * @param basePart The part to wrap.
     * @param mapper   The texture mapper to use.
     * @return The wrapped block model part.
     */
    private BlockModelPart wrapPart(BlockModelPart basePart, TextureMapper.BlockStateTextureMapper mapper) {
        return new TexturedBlockModelPart(basePart, mapper, sprites);
    }
}
