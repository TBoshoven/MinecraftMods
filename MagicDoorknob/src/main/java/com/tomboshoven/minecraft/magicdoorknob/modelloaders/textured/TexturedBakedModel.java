package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.ImmutableList;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BakedQuadRetextured;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A baked model that fills in the texture properties dynamically.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
class TexturedBakedModel<T extends IBakedModel> extends BakedModelWrapper<T> {
    // The baked texture getter
    private Function<? super ResourceLocation, ? extends TextureAtlasSprite> bakedTextureGetter;
    // The mapper that replaces property textures by their values
    private ITextureMapper textureMapper;

    /**
     * @param originalModel      The original baked model
     * @param bakedTextureGetter The baked texture getter
     * @param textureMapper      The mapper that replaces property textures by their values
     */
    TexturedBakedModel(T originalModel, Function<? super ResourceLocation, ? extends TextureAtlasSprite> bakedTextureGetter, ITextureMapper textureMapper) {
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
                ResourceLocation spriteLocation = textureMapper.mapSprite((PropertySprite) sprite, state, extraData);
                TextureAtlasSprite actualSprite = bakedTextureGetter.apply(spriteLocation);
                return new BakedQuadRetextured(quad, actualSprite);
            }
            return quad;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new TexturedOverrideList(super.getOverrides());
    }

    /**
     * We use override lists to dynamically texture items.
     */
    private class TexturedOverrideList extends ItemOverrideList {
        // The original baked model's override list
        private final ItemOverrideList wrappedOverrideList;

        /**
         * @param wrappedOverrideList The original baked model's override list
         */
        public TexturedOverrideList(ItemOverrideList wrappedOverrideList) {
            this.wrappedOverrideList = wrappedOverrideList;
        }

        @Override
        public ImmutableList<ItemOverride> getOverrides() {
            return wrappedOverrideList.getOverrides();
        }

        @Override
        public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
            // If the item has a texture mapper, use it.
            Item item = stack.getItem();
            if (item instanceof IItemStackTextureMapperProvider) {
                return new TexturedBakedModel<>(originalModel, bakedTextureGetter, ((IItemStackTextureMapperProvider) item).getTextureMapper(stack));
            }
            return wrappedOverrideList.getModelWithOverrides(originalModel, stack, world, entity);
        }
    }
}
