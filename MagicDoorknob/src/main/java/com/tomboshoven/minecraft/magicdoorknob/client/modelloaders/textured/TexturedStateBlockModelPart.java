package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.tomboshoven.minecraft.magicdoorknob.modeldata.MaterialInfoSource;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A block state model part that provides textures to its model.
 */
class TexturedStateBlockModelPart implements BlockStateModelPart {
    // The original block state model part to use with a replaced texture
    private final BlockStateModelPart original;
    // The material baker to use for obtaining baked materials from unbaked ones
    private final MaterialBaker materialBaker;
    // The mapper that replaces property textures by their values
    private final TextureMapper.BlockStateTextureMapper textureMapper;
    // The random source for texture lookups
    private final RandomSource randomSource;

    /**
     * @param original      The original block state model part
     * @param textureMapper The mapper that replaces property textures by their values
     * @param materialBaker The material baker to use for obtaining baked materials from unbaked ones
     * @param randomSource  The random source for texture lookups
     */
    TexturedStateBlockModelPart(BlockStateModelPart original, TextureMapper.BlockStateTextureMapper textureMapper, MaterialBaker materialBaker, RandomSource randomSource) {
        this.original = original;
        this.materialBaker = materialBaker;
        this.textureMapper = textureMapper;
        this.randomSource = randomSource;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction) {
        // Return the original quads, with the property sprites replaced by actual ones
        List<BakedQuad> quads = original.getQuads(direction);
        return quads.stream().map(quad -> {
            BakedQuad.MaterialInfo materialInfo = quad.materialInfo();
            if (materialInfo.sprite() instanceof PropertySprite property) {
                MaterialInfoSource materialInfoSource = textureMapper.mapSprite(property);
                if (materialInfoSource != null) {
                    BakedQuad.MaterialInfo lookupResult = materialInfoSource.lookupMaterialInfo(materialBaker, quad.direction(), randomSource);
                    return retexture(quad, lookupResult);
                }
                return null;
            }
            return quad;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static BakedQuad retexture(BakedQuad quad, BakedQuad.MaterialInfo materialInfo) {
        TextureAtlasSprite sprite = materialInfo.sprite();
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

        return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(), packedUV0, packedUV1, packedUV2, packedUV3, quad.direction(), materialInfo);
    }

    @Override
    public boolean useAmbientOcclusion() {
        //noinspection deprecation
        return original.useAmbientOcclusion();
    }

    @Override
    public Material.Baked particleMaterial() {
        Material.Baked particleMaterial = original.particleMaterial();
        if (particleMaterial.sprite() instanceof PropertySprite property) {
            MaterialInfoSource materialInfoSource = textureMapper.mapSprite(property);
            if (materialInfoSource != null) {
                return materialInfoSource.lookupMaterial(materialBaker, randomSource);
            }
        }
        return particleMaterial;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return original.materialFlags();
    }
}
