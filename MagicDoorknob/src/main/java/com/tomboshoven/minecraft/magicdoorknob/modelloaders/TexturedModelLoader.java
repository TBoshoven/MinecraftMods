package com.tomboshoven.minecraft.magicdoorknob.modelloaders;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tomboshoven.minecraft.magicdoorknob.properties.PropertyTexture;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TexturedModelLoader implements ICustomModelLoader {
    private static final String PROPERTY_NAMESPACE = "property";
    Map<ResourceLocation, ResourceLocation> baseModels = Maps.newHashMap();
    Set<ResourceLocation> extraTextures = Sets.newHashSet();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return baseModels.containsKey(new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath()));
    }

    public void register(ResourceLocation modelLocation, ResourceLocation baseModelLocation) {
        baseModels.put(modelLocation, baseModelLocation);
    }

    public void registerTexture(ResourceLocation textureLocation) {
        extraTextures.add(textureLocation);
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        ModelResourceLocation modelResourceLocation = (ModelResourceLocation) modelLocation;
        ResourceLocation baseResourceLocation = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath());
        ResourceLocation baseModelLocation = baseModels.getOrDefault(baseResourceLocation, ModelLoader.MODEL_MISSING);
        ResourceLocation augmentedBaseModelLocation = new ModelResourceLocation(baseModelLocation, modelResourceLocation.getVariant());
        // FIXME: This causes the untextured model to be cached and dummy textures to be requested
        IModel baseModel = ModelLoaderRegistry.getModel(augmentedBaseModelLocation);
        return new TexturedModel(baseModel, Collections.unmodifiableSet(extraTextures));
    }

    private class TexturedModel implements IModel {
        private final Set<ResourceLocation> extraTextures;
        IModel wrappedModel;

        TexturedModel(IModel wrappedModel, Set<ResourceLocation> extraTextures) {
            this.wrappedModel = wrappedModel;
            this.extraTextures = extraTextures;
        }

        @Override
        public Collection<ResourceLocation> getDependencies() {
            return wrappedModel.getDependencies();
        }

        @Override
        public Collection<ResourceLocation> getTextures() {
            Set<ResourceLocation> textures = wrappedModel.getTextures().stream()
                    .filter(location -> !PROPERTY_NAMESPACE.equals(location.getNamespace()))
                    .collect(Collectors.toSet());
            return Sets.union(textures, extraTextures);
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            Function<ResourceLocation, TextureAtlasSprite> augmentedTextureGetter = resourceLocation -> {
                if (PROPERTY_NAMESPACE.equals(resourceLocation.getNamespace())) {
                    return new PropertySprite(resourceLocation.getPath());
                }
                return bakedTextureGetter.apply(resourceLocation);
            };
            return new TexturedBakedModel(wrappedModel.bake(state, format, augmentedTextureGetter), bakedTextureGetter);
        }

        @Override
        public IModelState getDefaultState() {
            return wrappedModel.getDefaultState();
        }

        @Override
        public Optional<? extends IClip> getClip(String name) {
            return wrappedModel.getClip(name);
        }

        @Override
        public IModel process(ImmutableMap<String, String> customData) {
            return new TexturedModel(wrappedModel.process(customData), extraTextures);
        }

        @Override
        public IModel smoothLighting(boolean value) {
            return new TexturedModel(wrappedModel.smoothLighting(value), extraTextures);
        }

        @Override
        public IModel gui3d(boolean value) {
            return new TexturedModel(wrappedModel.gui3d(value), extraTextures);
        }

        @Override
        public IModel uvlock(boolean value) {
            return new TexturedModel(wrappedModel.uvlock(value), extraTextures);
        }

        @Override
        public IModel retexture(ImmutableMap<String, String> textures) {
            return new TexturedModel(wrappedModel.retexture(textures), extraTextures);
        }

        @Override
        public Optional<ModelBlock> asVanillaModel() {
            return wrappedModel.asVanillaModel();
        }

        private class PropertySprite extends TextureAtlasSprite {
            public PropertySprite(String name) {
                super(name);
                this.width = 16;
                this.height = 16;
                initSprite(16, 16, 0, 0, false);
            }
        }

        private class TexturedBakedModel implements IBakedModel {
            IBakedModel wrappedBakedModel;
            private Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

            public TexturedBakedModel(IBakedModel wrappedBakedModel, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
                this.wrappedBakedModel = wrappedBakedModel;
                this.bakedTextureGetter = bakedTextureGetter;
            }

            @Override
            public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
                List<BakedQuad> quads = wrappedBakedModel.getQuads(state, side, rand);
                if (!(state instanceof IExtendedBlockState)) {
                    return quads;
                }
                return quads.stream().map(quad -> {
                    TextureAtlasSprite sprite = quad.getSprite();
                    if (sprite instanceof PropertySprite) {
                        TextureAtlasSprite actualSprite = mapSprite((PropertySprite)sprite, (IExtendedBlockState) state);
                        return new BakedQuadRetextured(quad, actualSprite);
                    }
                    return quad;
                }).collect(Collectors.toList());
            }

            TextureAtlasSprite mapSprite(PropertySprite spriteToMap, IExtendedBlockState blockState) {
                String name = spriteToMap.getIconName();
                PropertyTexture property = new PropertyTexture(name);
                ResourceLocation spriteLocation = blockState.getValue(property);
                if (spriteLocation == null) {
                    spriteLocation = new ResourceLocation("missingno");
                }
                return bakedTextureGetter.apply(spriteLocation);
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
                return wrappedBakedModel.getOverrides();
            }

            @Override
            public boolean isAmbientOcclusion(IBlockState state) {
                return wrappedBakedModel.isAmbientOcclusion();
            }

            @Override
            public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
                return wrappedBakedModel.handlePerspective(cameraTransformType);
            }
        }
    }
}
