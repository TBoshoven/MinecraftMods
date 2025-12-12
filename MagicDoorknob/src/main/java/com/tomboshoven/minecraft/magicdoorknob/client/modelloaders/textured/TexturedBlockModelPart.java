package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.TextureSourceReference;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A block model part that provides textures to its model.
 */
class TexturedBlockModelPart implements BlockModelPart {
    // The original block model part to use with a replaced texture
    private final BlockModelPart original;
    // The texture getter
    private final SpriteGetter sprites;
    // The mapper that replaces property textures by their values
    private final TextureMapper.BlockStateTextureMapper textureMapper;
    // The random source for texture lookups
    private final RandomSource randomSource;

    // The vertex format of the model. At the moment, only "block" is supported.
    private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormat.BLOCK;

    /**
     * @param original      The original block model part
     * @param textureMapper The mapper that replaces property textures by their values
     * @param sprites       The sprite getter
     * @param randomSource  The random source for texture lookups
     */
    TexturedBlockModelPart(BlockModelPart original, TextureMapper.BlockStateTextureMapper textureMapper, SpriteGetter sprites, RandomSource randomSource) {
        this.original = original;
        this.sprites = sprites;
        this.textureMapper = textureMapper;
        this.randomSource = randomSource;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction) {
        // Return the original quads, with the property sprites replaced by actual ones
        List<BakedQuad> quads = original.getQuads(direction);
        return quads.stream().map(quad -> {
            TextureAtlasSprite sprite = quad.sprite();
            if (sprite instanceof PropertySprite property) {
                TextureSourceReference textureSourceReference = textureMapper.mapSprite(property);
                if (textureSourceReference != null) {
                    TextureSourceReference.LookupResult lookupResult = textureSourceReference.lookup(sprites, quad.direction(), randomSource);
                    return retexture(quad, lookupResult.sprite(), lookupResult.tintIndex());
                }
                return null;
            }
            return quad;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite sprite, @Nullable Integer tintIndex) {
        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        long packedUV0 = quad.packedUV0();
        long packedUV1 = quad.packedUV1();
        long packedUV2 = quad.packedUV2();
        long packedUV3 = quad.packedUV3();
        packedUV0 = UVPair.pack(minU + (maxU - minU) * UVPair.unpackU(packedUV0), minV + (maxV - minV) * UVPair.unpackV(packedUV0));
        packedUV1 = UVPair.pack(minU + (maxU - minU) * UVPair.unpackU(packedUV1), minV + (maxV - minV) * UVPair.unpackV(packedUV1));
        packedUV2 = UVPair.pack(minU + (maxU - minU) * UVPair.unpackU(packedUV2), minV + (maxV - minV) * UVPair.unpackV(packedUV2));
        packedUV3 = UVPair.pack(minU + (maxU - minU) * UVPair.unpackU(packedUV3), minV + (maxV - minV) * UVPair.unpackV(packedUV3));

        return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(), packedUV0, packedUV1, packedUV2, packedUV3, tintIndex == null ? quad.tintIndex() : tintIndex, quad.direction(), sprite, quad.shade(), quad.lightEmission(), quad.bakedNormals(), quad.bakedColors(), quad.hasAmbientOcclusion());
    }

    @Override
    public boolean useAmbientOcclusion() {
        //noinspection deprecation
        return original.useAmbientOcclusion();
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        TextureAtlasSprite icon = original.particleIcon();
        if (icon instanceof PropertySprite property) {
            TextureSourceReference textureSourceReference = textureMapper.mapSprite(property);
            if (textureSourceReference != null) {
                return textureSourceReference.lookup(sprites, randomSource).sprite();
            }
        }
        return icon;
    }

    @Override
    public ChunkSectionLayer getRenderType(BlockState state) {
        return original.getRenderType(state);
    }
}
