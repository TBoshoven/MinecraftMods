package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.ImmutableList;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BakedQuadRetextured;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.vecmath.Matrix4f;
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
class TexturedBakedModel implements IBakedModel {
    // The original baked model
    private IBakedModel wrappedBakedModel;
    // The baked texture getter
    private Function<? super ResourceLocation, ? extends TextureAtlasSprite> bakedTextureGetter;
    // The mapper that replaces property textures by their values
    private ITextureMapper textureMapper;

    /**
     * @param wrappedBakedModel  The original baked model
     * @param bakedTextureGetter The baked texture getter
     * @param textureMapper      The mapper that replaces property textures by their values
     */
    TexturedBakedModel(IBakedModel wrappedBakedModel, Function<? super ResourceLocation, ? extends TextureAtlasSprite> bakedTextureGetter, ITextureMapper textureMapper) {
        this.wrappedBakedModel = wrappedBakedModel;
        this.bakedTextureGetter = bakedTextureGetter;
        this.textureMapper = textureMapper;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        // Return the original quads, with the property sprites replaced by actual ones
        List<BakedQuad> quads = wrappedBakedModel.getQuads(state, side, rand);
        return quads.stream().map(quad -> {
            TextureAtlasSprite sprite = quad.getSprite();
            if (sprite instanceof PropertySprite) {
                ResourceLocation spriteLocation = textureMapper.mapSprite((PropertySprite) sprite, (IExtendedBlockState) state);
                TextureAtlasSprite actualSprite = bakedTextureGetter.apply(spriteLocation);
                return new BakedQuadRetextured(quad, actualSprite);
            }
            return quad;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isAmbientOcclusion() {
        return wrappedBakedModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return wrappedBakedModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return wrappedBakedModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return wrappedBakedModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return wrappedBakedModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new TexturedOverrideList(wrappedBakedModel.getOverrides());
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return wrappedBakedModel.isAmbientOcclusion();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        // No easy way to fix the first element of the tuple, but it's very rarely different from the original baked
        // model, so we just replace it by this.
        Pair<? extends IBakedModel, Matrix4f> result = wrappedBakedModel.handlePerspective(cameraTransformType);
        return Pair.of(this, result.getRight());
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
                return new TexturedBakedModel(wrappedBakedModel, bakedTextureGetter, ((IItemStackTextureMapperProvider) item).getTextureMapper(stack));
            }
            return wrappedOverrideList.getModelWithOverrides(originalModel, stack, world, entity);
        }
    }
}
