package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A block model part that  provides textures to its model.
 */
class TexturedBlockModelPart implements BlockModelPart {
    // The original block model part to use with a replaced texture
    private final BlockModelPart original;
    // The texture getter
    private final SpriteGetter sprites;
    // The mapper that replaces property textures by their values
    private final TextureMapper.BlockStateTextureMapper textureMapper;

    // The vertex format of the model. At the moment, only "block" is supported.
    private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormat.BLOCK;

    /**
     * @param original      The original block model part
     * @param textureMapper The mapper that replaces property textures by their values
     * @param sprites       The sprite getter
     */
    TexturedBlockModelPart(BlockModelPart original, TextureMapper.BlockStateTextureMapper textureMapper, SpriteGetter sprites) {
        this.original = original;
        this.sprites = sprites;
        this.textureMapper = textureMapper;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction) {
        // Return the original quads, with the property sprites replaced by actual ones
        List<BakedQuad> quads = original.getQuads(direction);
        return quads.stream().map(quad -> {
            TextureAtlasSprite sprite = quad.sprite();
            if (sprite instanceof PropertySprite property) {
                Material material = textureMapper.mapSprite(property);
                if (material == null) {
                    //noinspection ReturnOfNull
                    return null;
                }
                TextureAtlasSprite actualSprite = sprites.get(material, () -> "TexturedBlockModelPart");
                return retexture(quad, actualSprite);
            }
            return quad;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite sprite) {
        // Note: assumes original sprite is 1x1
        int[] vertexData = quad.vertices().clone();

        // Offset between vertices
        int stride = VERTEX_FORMAT.getVertexSize();
        // Offset for a single U/V
        int eltOffset = VertexFormatElement.UV0.type().size();

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        int idx = VERTEX_FORMAT.getOffset(VertexFormatElement.UV0);
        // Iterate over all 4 vertices
        for (int vertex = 0; vertex < 4; ++vertex) {
            float vertexU = Float.intBitsToFloat(getAtByteOffset(vertexData, idx));
            float vertexV = Float.intBitsToFloat(getAtByteOffset(vertexData, idx + eltOffset));
            float newU = minU + (maxU - minU) * vertexU;
            float newV = minV + (maxV - minV) * vertexV;
            putAtByteOffset(vertexData, idx, Float.floatToRawIntBits(newU));
            putAtByteOffset(vertexData, idx + eltOffset, Float.floatToRawIntBits(newV));

            idx += stride;
        }

        return new BakedQuad(vertexData, quad.tintIndex(), quad.direction(), sprite, quad.shade(), quad.lightEmission(), quad.hasAmbientOcclusion());
    }

    private static int getAtByteOffset(int[] inData, int offset) {
        // Borrowed from QuadTransformer code
        int index = offset / 4;
        int lsb = inData[index];

        int shift = (offset % 4) * 8;
        if (shift == 0)
            return inData[index];

        int msb = inData[index + 1];

        return (lsb >>> shift) | (msb << (32 - shift));
    }

    private static void putAtByteOffset(int[] outData, int offset, int value) {
        // Borrowed from QuadTransformer code
        int index = offset / 4;
        int shift = (offset % 4) * 8;

        if (shift == 0) {
            outData[index] = value;
            return;
        }

        int lsbMask = 0xFFFFFFFF >>> (32 - shift);
        int msbMask = 0xFFFFFFFF << shift;

        outData[index] = (outData[index] & lsbMask) | (value << shift);
        outData[index + 1] = (outData[index + 1] & msbMask) | (value >>> (32 - shift));
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
            Material spriteLocation = textureMapper.mapSprite(property);
            if (spriteLocation == null) {
                return icon;
            }
            icon = sprites.get(spriteLocation, () -> "TexturedBlockModelPart");
        }
        return icon;
    }

    @Override
    public RenderType getRenderType(BlockState state) {
        return original.getRenderType(state);
    }
}
