package com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
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
        VERTEX_FORMAT_ELEMENT_UV = VERTEX_FORMAT.getElements().stream().filter(el -> el.usage() == VertexFormatElement.Usage.UV && el.index() == 0).findFirst().orElseThrow();
        VERTEX_FORMAT_ELEMENT_UV_OFFSET = VERTEX_FORMAT.getOffset(VERTEX_FORMAT_ELEMENT_UV);
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
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        // Return the original quads, with the property sprites replaced by actual ones
        List<BakedQuad> quads = originalModel.getQuads(state, side, rand, extraData, renderType);
        return quads.stream().map(quad -> {
            TextureAtlasSprite sprite = quad.getSprite();
            if (sprite instanceof PropertySprite) {
                Material material = textureMapper.mapSprite((PropertySprite) sprite, state, extraData);
                if (material == null) {
                    return null;
                }
                TextureAtlasSprite actualSprite = bakedTextureGetter.apply(material);
                return retexture(quad, actualSprite);
            }
            return quad;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static BakedQuad retexture(BakedQuad quad, TextureAtlasSprite sprite) {
        // Note: assumes original sprite is 1x1
        int[] vertexData = quad.getVertices().clone();

        // Offset between vertices
        int stride = VERTEX_FORMAT.getVertexSize();
        // Offset for a single U/V
        int eltOffset = VERTEX_FORMAT_ELEMENT_UV.type().size();

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        int idx = VERTEX_FORMAT_ELEMENT_UV_OFFSET;
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

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
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
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return getQuads(state, side, rand, ModelData.EMPTY, null);
    }

    @Override
    public ItemOverrides getOverrides() {
        return new TexturedOverrideList(super.getOverrides());
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull ModelData data) {
        TextureAtlasSprite sprite = super.getParticleIcon(data);
        if (sprite instanceof PropertySprite) {
            Material spriteLocation = textureMapper.mapSprite((PropertySprite) sprite, null, data);
            if (spriteLocation == null) {
                spriteLocation = new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation());
            }
            sprite = bakedTextureGetter.apply(spriteLocation);
        }
        return sprite;
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
