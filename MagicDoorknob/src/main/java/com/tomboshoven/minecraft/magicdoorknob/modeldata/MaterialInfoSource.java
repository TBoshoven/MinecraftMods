package com.tomboshoven.minecraft.magicdoorknob.modeldata;

import com.mojang.blaze3d.platform.Transparency;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * A something that evaluates to a specific baked material, such as an unbaked material, or a block in the world.
 * <p>
 * This is capable of performing lookups resulting in baked materials, or material info, to cover different use cases.
 */
public interface MaterialInfoSource {
    /**
     * Perform the actual lookup.
     *
     * @param materialBaker The material baker to use for obtaining the required material.
     * @return The result of the lookup.
     */
    default Material.Baked lookupMaterial(MaterialBaker materialBaker) {
        return lookupMaterial(materialBaker, null);
    }

    /**
     * Perform the actual lookup.
     *
     * @param materialBaker The material baker to use for obtaining the required material.
     * @return The result of the lookup.
     */
    default BakedQuad.MaterialInfo lookupMaterialInfo(MaterialBaker materialBaker) {
        return lookupMaterialInfo(materialBaker, null);
    }

    /**
     * Perform the actual lookup.
     *
     * @param materialBaker The material baker to use for obtaining the required material.
     * @param randomSource  A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    default Material.Baked lookupMaterial(MaterialBaker materialBaker, @Nullable RandomSource randomSource) {
        return lookupMaterial(materialBaker, Direction.NORTH, randomSource);
    }

    /**
     * Perform the actual lookup.
     *
     * @param materialBaker The material baker to use for obtaining the required material.
     * @param randomSource  A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    default BakedQuad.MaterialInfo lookupMaterialInfo(MaterialBaker materialBaker, @Nullable RandomSource randomSource) {
        return lookupMaterialInfo(materialBaker, Direction.NORTH, randomSource);
    }

    /**
     * Perform the actual lookup.
     *
     * @param materialBaker The material baker to use for obtaining the required material.
     * @param direction     The direction of the side to get the texture for.
     * @param randomSource  A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    Material.Baked lookupMaterial(MaterialBaker materialBaker, Direction direction, @Nullable RandomSource randomSource);

    /**
     * Perform the actual lookup.
     *
     * @param materialBaker The material baker to use for obtaining the required material.
     * @param direction     The direction of the side to get the texture for.
     * @param randomSource  A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    BakedQuad.MaterialInfo lookupMaterialInfo(MaterialBaker materialBaker, Direction direction, @Nullable RandomSource randomSource);

    /**
     * A direct reference to a material.
     *
     * @param material         The material to use.
     * @param transparency     The transparency of the material
     * @param tintIndex        The tint index of the material (-1 if not applicable)
     * @param shade            The shade value of the material
     * @param lightEmission    The light emission of the material
     * @param ambientOcclusion Whether ambient occlusion applies to the material
     */
    record MaterialTextureSource(Material material, Transparency transparency, int tintIndex, boolean shade,
                                 int lightEmission, boolean ambientOcclusion) implements MaterialInfoSource {
        @Override
        public Material.Baked lookupMaterial(MaterialBaker materialBaker, Direction direction, @Nullable RandomSource randomSource) {
            return materialBaker.get(material, () -> "TextureReference");
        }

        @Override
        public BakedQuad.MaterialInfo lookupMaterialInfo(MaterialBaker materialBaker, Direction direction, @Nullable RandomSource randomSource) {
            Material.Baked baked = lookupMaterial(materialBaker, direction, randomSource);
            return BakedQuad.MaterialInfo.of(baked, transparency, tintIndex, shade, lightEmission, ambientOcclusion);
        }
    }

    /**
     * A reference to a particle for a block in the world.
     * The block doesn't actually need to exist in the level at the provided position, but if the model relies on model
     * data, the result may not be great.
     *
     * @param level      The level (hypothetically) containing the block. May be left null for an agnostic lookup.
     * @param pos        The position of the block in the level.
     * @param blockState The block state of the block.
     * @param fallback   A fallback to use in case no appropriate textures are found on the block model.
     */
    record BlockParticle(@Nullable BlockAndTintGetter level, BlockPos pos, BlockState blockState,
                         MaterialInfoSource fallback) implements MaterialInfoSource {
        private Material.Baked lookupMaterialNoFallback() {
            Minecraft minecraft = Minecraft.getInstance();
            BlockStateModelSet blockStateModelSet = minecraft.getModelManager().getBlockStateModelSet();
            BlockStateModel blockModel = blockStateModelSet.get(blockState);

            //noinspection deprecation
            return level == null ? blockModel.particleMaterial() : blockModel.particleMaterial(level, pos, blockState);
        }

        private static boolean isFallback(TextureAtlasSprite sprite) {
            Minecraft minecraft = Minecraft.getInstance();
            return sprite == minecraft.getAtlasManager().getAtlasOrThrow(sprite.atlasLocation()).getSprite(MissingTextureAtlasSprite.getLocation());
        }

        @Override
        public Material.Baked lookupMaterial(MaterialBaker materialBaker, Direction direction, @Nullable RandomSource randomSource) {
            Material.Baked particleMaterial = lookupMaterialNoFallback();
            if (isFallback(particleMaterial.sprite())) {
                return fallback.lookupMaterial(materialBaker, direction, randomSource);
            }
            return particleMaterial;
        }

        @Override
        public BakedQuad.MaterialInfo lookupMaterialInfo(MaterialBaker materialBaker, Direction direction, @Nullable RandomSource randomSource) {
            Material.Baked particleMaterial = lookupMaterialNoFallback();
            if (isFallback(particleMaterial.sprite())) {
                return fallback.lookupMaterialInfo(materialBaker, direction, randomSource);
            }
            Transparency transparency = particleMaterial.forceTranslucent() ? Transparency.TRANSLUCENT : particleMaterial.sprite().transparency();

            return BakedQuad.MaterialInfo.of(particleMaterial, transparency, -1, false, 0, true);
        }
    }

    /**
     * A reference to a block in the world.
     * The block doesn't actually need to exist in the level at the provided position, but if the model relies on model
     * data, the result may not be great.
     * If the block is textured, its textures are looked up on a best-effort basis.
     *
     * @param level      The level (hypothetically) containing the block. May be left null for an agnostic lookup.
     * @param pos        The position of the block in the level.
     * @param blockState The block state of the block.
     * @param fallback   A fallback to use in case no appropriate textures are found on the block model.
     */
    record BlockLookup(@Nullable BlockAndTintGetter level, BlockPos pos, BlockState blockState,
                       MaterialInfoSource fallback) implements MaterialInfoSource {
        private BakedQuad.@Nullable MaterialInfo lookupNoFallback(Direction direction, @Nullable RandomSource randomSource) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockStateModelSet blockStateModelSet = minecraft.getModelManager().getBlockStateModelSet();
            BlockStateModel blockModel = blockStateModelSet.get(blockState);
            List<BlockStateModelPart> parts = new ObjectArrayList<>();
            if (randomSource == null) {
                randomSource = RandomSource.create();
            }
            if (level == null) {
                //noinspection deprecation
                blockModel.collectParts(randomSource, parts);
            } else {
                blockModel.collectParts(level, pos, blockState, randomSource, parts);
            }
            BakedQuad.MaterialInfo result = null;
            for (BlockStateModelPart part : parts) {
                // First try the actual direction we're going for.
                for (BakedQuad quad : part.getQuads(direction)) {
                    return quad.materialInfo();
                }
                // Fall back on any other ones, if they're there.
                // This will make cross blocks textures a bit better.
                for (BakedQuad quad : part.getQuads(null)) {
                    // Separate out the sides from top and bottom, because those are often not compatible
                    Direction quadDirection = quad.direction();
                    if (quadDirection.getAxis().isVertical() != direction.getAxis().isVertical()) {
                        continue;
                    }
                    result = quad.materialInfo();
                    // Always prefer a direction match
                    if (quadDirection == direction) {
                        return result;
                    }
                }
            }
            return result;
        }

        @Override
        public Material.Baked lookupMaterial(MaterialBaker materialBaker, Direction direction, @Nullable RandomSource randomSource) {
            BakedQuad.MaterialInfo result = lookupNoFallback(direction, randomSource);
            if (result == null) {
                return fallback.lookupMaterial(materialBaker, direction, randomSource);
            }
            return new Material.Baked(result.sprite(), false);
        }

        @Override
        public BakedQuad.MaterialInfo lookupMaterialInfo(MaterialBaker materialBaker, Direction direction, @Nullable RandomSource randomSource) {
            BakedQuad.MaterialInfo result = lookupNoFallback(direction, randomSource);
            if (result == null) {
                return fallback.lookupMaterialInfo(materialBaker, direction, randomSource);
            }
            return result;
        }
    }
}
