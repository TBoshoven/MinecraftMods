package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.tomboshoven.minecraft.magicdoorknob.modeldata.TextureSourceReference;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A baked model that fills in the texture properties dynamically.
 */
class TexturedBakedModel<T extends BakedModel> extends BakedModelWrapper<T> {
    // The baked texture getter
    private final Function<? super Material, ? extends TextureAtlasSprite> bakedTextureGetter;
    // The mapper that replaces property textures by their values
    private final ITextureMapper textureMapper;

    // The vertex format of the model. At the moment, only "block" is supported.
    private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormat.BLOCK;
    // The vertex format element containing the first texture UV coordinates
    private static final VertexFormatElement VERTEX_FORMAT_ELEMENT_UV;
    // The offset of the UV coordinates in the vertex format
    private static final int VERTEX_FORMAT_ELEMENT_UV_OFFSET;

    static {
        // Find the UV index and offset in the vertex format.
        int index;
        VertexFormatElement element = null;
        ImmutableList<VertexFormatElement> elements = VERTEX_FORMAT.getElements();
        int size = elements.size();
        for (index = 0; index < size; ++index) {
            VertexFormatElement el = VERTEX_FORMAT.getElements().get(index);
            if (el.getUsage() == VertexFormatElement.Usage.UV && el.getIndex() == 0) {
                element = el;
                break;
            }
        }
        VERTEX_FORMAT_ELEMENT_UV = element;
        VERTEX_FORMAT_ELEMENT_UV_OFFSET = VERTEX_FORMAT.getOffset(index);
    }

    /**
     * @param originalModel      The original baked model
     * @param bakedTextureGetter The baked texture getter
     * @param textureMapper      The mapper that replaces property textures by their values
     */
    TexturedBakedModel(T originalModel, Function<? super Material, ? extends TextureAtlasSprite> bakedTextureGetter, ITextureMapper textureMapper) {
        super(originalModel);
        this.bakedTextureGetter = bakedTextureGetter;
        this.textureMapper = textureMapper;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
        // Return the original quads, with the property sprites replaced by actual ones
        List<BakedQuad> quads = originalModel.getQuads(state, side, rand, extraData);
        return quads.stream().map(quad -> {
            TextureAtlasSprite sprite = quad.getSprite();
            if (sprite instanceof PropertySprite) {
                TextureSourceReference textureSourceReference = textureMapper.mapSprite((PropertySprite) sprite, state, extraData);
                TextureSourceReference.LookupResult lookupResult = textureSourceReference.lookup(bakedTextureGetter, quad.getDirection(), rand);
                return retexture(quad, lookupResult.sprite(), lookupResult.tintIndex());
            }
            return quad;
        }).collect(Collectors.toList());
    }

    private static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite sprite, @Nullable Integer tintIndex) {
        int[] vertexData = quad.getVertices().clone();

        // Offset between vertices
        int stride = VERTEX_FORMAT.getVertexSize();
        // Offset for a single U/V
        int eltOffset = VERTEX_FORMAT_ELEMENT_UV.getType().getSize();

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float uDiff = maxU - minU;
        float minV = sprite.getV0();
        float maxV = sprite.getV1();
        float vDiff = maxV - minV;

        int idx = VERTEX_FORMAT_ELEMENT_UV_OFFSET;
        // Iterate over all 4 vertices
        for (int vertex = 0; vertex < 4; ++vertex) {
            float vertexU = Float.intBitsToFloat(getAtByteOffset(vertexData, idx));
            float vertexV = Float.intBitsToFloat(getAtByteOffset(vertexData, idx + eltOffset));
            putAtByteOffset(vertexData, idx, Float.floatToRawIntBits(minU + uDiff * vertexU));
            putAtByteOffset(vertexData, idx + eltOffset, Float.floatToRawIntBits(minV + vDiff * vertexV));
            idx += stride;
        }

        return new BakedQuad(vertexData, tintIndex == null ? quad.getTintIndex() : tintIndex, quad.getDirection(), sprite, quad.isShade());
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
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Override
    public ItemOverrides getOverrides() {
        return new TexturedOverrideList(super.getOverrides());
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) {
        TextureAtlasSprite icon = super.getParticleIcon(data);
        if (icon instanceof PropertySprite) {
            TextureSourceReference textureSourceReference = textureMapper.mapSprite((PropertySprite) icon, null, data);
            return textureSourceReference.lookup(bakedTextureGetter).sprite();
        }
        return icon;
    }

    /**
     * We use override lists to dynamically texture items.
     */
    private class TexturedOverrideList extends ItemOverrides {
        // The original baked model's override list
        private final ItemOverrides wrappedOverrideList;

        /**
         * @param wrappedOverrideList The original baked model's override list
         */
        TexturedOverrideList(ItemOverrides wrappedOverrideList) {
            this.wrappedOverrideList = wrappedOverrideList;
        }

        @Override
        public com.google.common.collect.ImmutableList<BakedOverride> getOverrides() {
            return wrappedOverrideList.getOverrides();
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity entityIn, int seed) {
            // If the item has a texture mapper, use it.
            Item item = stack.getItem();
            if (item instanceof IItemStackTextureMapperProvider) {
                return new TexturedBakedModel<>(model, bakedTextureGetter, ((IItemStackTextureMapperProvider) item).getTextureMapper(stack));
            }
            return wrappedOverrideList.resolve(model, stack, worldIn, entityIn, seed);
        }
    }
}
