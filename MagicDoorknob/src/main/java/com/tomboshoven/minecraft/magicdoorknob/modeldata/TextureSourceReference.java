package com.tomboshoven.minecraft.magicdoorknob.modeldata;


import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;

/**
 * A reference to something that is associated with a texture, such as a material or a block in the world.
 */
public interface TextureSourceReference {
    /**
     * The result of a lookup, containing a sprite and optionally a tint index to use.
     */
    class LookupResult {
        TextureAtlasSprite sprite;
        @Nullable
        Integer tintIndex;

        /**
         * @param sprite    The sprite resulting from the lookup.
         * @param tintIndex The tint index to use, if any was found in the source.
         */
        public LookupResult(TextureAtlasSprite sprite, @Nullable Integer tintIndex) {
            this.sprite = sprite;
            this.tintIndex = tintIndex;
        }

        public TextureAtlasSprite sprite() {
            return sprite;
        }

        public @Nullable Integer tintIndex() {
            return tintIndex;
        }
    }

    /**
     * Perform the actual lookup.
     *
     * @param sprites The sprite getter to use for looking up texture references.
     * @return The result of the lookup.
     */
    default LookupResult lookup(Function<? super RenderMaterial, ? extends TextureAtlasSprite> sprites) {
        return lookup(sprites, null);
    }

    /**
     * Perform the actual lookup.
     *
     * @param sprites The sprite getter to use for looking up texture references.
     * @param random  A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    default LookupResult lookup(Function<? super RenderMaterial, ? extends TextureAtlasSprite> sprites, @Nullable Random random) {
        return lookup(sprites, Direction.NORTH, random);
    }

    /**
     * Perform the actual lookup.
     *
     * @param sprites   The sprite getter to use for looking up texture references.
     * @param direction The direction of the side to get the texture for.
     * @param random    A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    LookupResult lookup(Function<? super RenderMaterial, ? extends TextureAtlasSprite> sprites, Direction direction, @Nullable Random random);

    /**
     * A direct reference to a material.
     */
    class MaterialTextureSource implements TextureSourceReference {
        RenderMaterial material;

        /**
         * @param material The material referring to the texture to use.
         */
        public MaterialTextureSource(RenderMaterial material) {
            this.material = material;
        }

        @Override
        public LookupResult lookup(Function<? super RenderMaterial, ? extends TextureAtlasSprite> sprites, Direction direction, @Nullable Random random) {
            return new LookupResult(sprites.apply(material), null);
        }
    }

    /**
     * A reference to a particle for a block in the world.
     * The block doesn't actually need to exist in the level at the provided position, but if the model relies on model
     * data, the result may not be great.
     */
    class BlockParticle implements TextureSourceReference {
        BlockState blockState;

        /**
         * @param blockState The block state of the block.
         */
        public BlockParticle(BlockState blockState) {
            this.blockState = blockState;
        }

        @Override
        public LookupResult lookup(Function<? super RenderMaterial, ? extends TextureAtlasSprite> sprites, Direction direction, @Nullable Random random) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockModelShapes blockModelShaper = minecraft.getBlockRenderer().getBlockModelShaper();
            IBakedModel blockModel = blockModelShaper.getBlockModel(blockState);
            //noinspection deprecation
            return new LookupResult(blockModel.getParticleIcon(), null);
        }
    }

    /**
     * A direct reference to a material.
     */
    class BlockLookup implements TextureSourceReference {
        @Nullable
        World level;
        BlockPos pos;
        BlockState blockState;
        TextureSourceReference fallback;

        /**
         * @param level      The level (hypothetically) containing the block. May be left null for an agnostic lookup.
         * @param pos        The position of the block in the level.
         * @param blockState The block state of the block.
         * @param fallback   A fallback to use in case no appropriate textures are found on the block model.
         */
        public BlockLookup(@Nullable World level, BlockPos pos, BlockState blockState,
                           TextureSourceReference fallback) {
            this.level = level;
            this.pos = pos;
            this.blockState = blockState;
            this.fallback = fallback;
        }

        @Override
        public LookupResult lookup(Function<? super RenderMaterial, ? extends TextureAtlasSprite> sprites, Direction direction, @Nullable Random random) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockModelShapes blockModelShaper = minecraft.getBlockRenderer().getBlockModelShaper();
            IBakedModel blockModel = blockModelShaper.getBlockModel(blockState);
            if (random == null) {
                random = new Random();
            }
            // First try the actual direction we're going for.
            for (BakedQuad quad : blockModel.getQuads(blockState, direction, random, EmptyModelData.INSTANCE)) {
                return new LookupResult(quad.getSprite(), quad.getTintIndex());
            }
            // Fall back on any other ones, if they're there.
            // This will make cross blocks textures a bit better.
            LookupResult result = null;
            for (BakedQuad quad : blockModel.getQuads(blockState, null, random, EmptyModelData.INSTANCE)) {
                // Separate out the sides from top and bottom, because those are often not compatible
                Direction quadDirection = quad.getDirection();
                if (quadDirection.getAxis().isVertical() != direction.getAxis().isVertical()) {
                    continue;
                }
                // Always prefer a direction match
                result = new LookupResult(quad.getSprite(), quad.getTintIndex());
                if (quadDirection == direction) {
                    return result;
                }
            }
            if (result == null) {
                return fallback.lookup(sprites, direction, random);
            }
            return result;
        }
    }
}
