package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class TexturedBakedModel implements IBakedModel {
    private IBakedModel wrappedBakedModel;
    private Function<? super ResourceLocation, ? extends TextureAtlasSprite> bakedTextureGetter;
    private ITextureMapper textureMapper;

    TexturedBakedModel(IBakedModel wrappedBakedModel, Function<? super ResourceLocation, ? extends TextureAtlasSprite> bakedTextureGetter, ITextureMapper textureMapper) {
        this.wrappedBakedModel = wrappedBakedModel;
        this.bakedTextureGetter = bakedTextureGetter;
        this.textureMapper = textureMapper;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
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
    public boolean isAmbientOcclusion(IBlockState state) {
        return wrappedBakedModel.isAmbientOcclusion();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IBakedModel, Matrix4f> result = wrappedBakedModel.handlePerspective(cameraTransformType);
        return Pair.of(this, result.getRight());
    }

    private class TexturedOverrideList extends ItemOverrideList {
        private final ItemOverrideList wrappedOverrideList;

        public TexturedOverrideList(ItemOverrideList wrappedOverrideList) {
            super(Lists.newArrayList());
            this.wrappedOverrideList = wrappedOverrideList;
        }

        @Nullable
        @Override
        public ResourceLocation applyOverride(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return wrappedOverrideList.applyOverride(stack, worldIn, entityIn);
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            Item item = stack.getItem();
            if (item instanceof IItemStackTextureMapperProvider) {
                return new TexturedBakedModel(wrappedBakedModel, bakedTextureGetter, ((IItemStackTextureMapperProvider) item).getTextureMapper(stack));
            }
            return wrappedOverrideList.handleItemState(originalModel, stack, world, entity);
        }
    }
}
