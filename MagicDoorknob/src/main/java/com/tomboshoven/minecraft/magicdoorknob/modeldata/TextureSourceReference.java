package com.tomboshoven.minecraft.magicdoorknob.modeldata;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A reference to something that is associated with a texture, such as a material or a block in the world.
 */
public interface TextureSourceReference {
    /**
     * The result of a lookup, containing a sprite and optionally a tint index to use.
     *
     * @param sprite    The sprite resulting from the lookup.
     * @param tintIndex The tint index to use, if any was found in the source.
     */
    record LookupResult(TextureAtlasSprite sprite, @Nullable Integer tintIndex) {
    }

    /**
     * Perform the actual lookup.
     *
     * @param sprites The sprite getter to use for looking up texture references.
     * @return The result of the lookup.
     */
    default LookupResult lookup(SpriteGetter sprites) {
        return lookup(sprites, null);
    }

    /**
     * Perform the actual lookup.
     *
     * @param sprites The sprite getter to use for looking up texture references.
     * @param randomSource A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    default LookupResult lookup(SpriteGetter sprites, @Nullable RandomSource randomSource) {
        return lookup(sprites, Direction.NORTH, randomSource);
    }

    /**
     * Perform the actual lookup.
     *
     * @param sprites      The sprite getter to use for looking up texture references.
     * @param direction    The direction of the side to get the texture for.
     * @param randomSource A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    LookupResult lookup(SpriteGetter sprites, Direction direction, @Nullable RandomSource randomSource);

    /**
     * A direct reference to a material.
     *
     * @param material The material referring to the texture to use.
     */
    record MaterialTextureSource(Material material) implements TextureSourceReference {
        @Override
        public LookupResult lookup(SpriteGetter sprites, Direction direction, @Nullable RandomSource randomSource) {
            return new LookupResult(sprites.get(material, () -> "TextureReference"), null);
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
    record BlockParticle(@Nullable Level level, BlockPos pos, BlockState blockState, TextureSourceReference fallback) implements TextureSourceReference {
        @Override
        public LookupResult lookup(SpriteGetter sprites, Direction direction, @Nullable RandomSource randomSource) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockModelShaper blockModelShaper = minecraft.getBlockRenderer().getBlockModelShaper();
            BlockStateModel blockModel = blockModelShaper.getBlockModel(blockState);

            //noinspection deprecation
            TextureAtlasSprite icon = level == null ? blockModel.particleIcon() : blockModel.particleIcon(level, pos, blockState);
            if (icon == minecraft.getTextureAtlas(icon.atlasLocation()).apply(MissingTextureAtlasSprite.getLocation())) {
                return fallback.lookup(sprites, direction, randomSource);
            }
            return new LookupResult(icon, null);
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
    record BlockLookup(@Nullable Level level, BlockPos pos, BlockState blockState,
                       TextureSourceReference fallback) implements TextureSourceReference {
        @Override
        public LookupResult lookup(SpriteGetter sprites, Direction direction, @Nullable RandomSource randomSource) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockModelShaper blockModelShaper = minecraft.getBlockRenderer().getBlockModelShaper();
            BlockStateModel blockModel = blockModelShaper.getBlockModel(blockState);
            List<BlockModelPart> parts = new ObjectArrayList<>();
            if (randomSource == null) {
                randomSource = RandomSource.create();
            }
            if (level == null) {
                //noinspection deprecation
                blockModel.collectParts(randomSource, parts);
            } else {
                blockModel.collectParts(level, pos, blockState, randomSource, parts);
            }
            LookupResult result = null;
            for (BlockModelPart part : parts) {
                // First try the actual direction we're going for.
                for (BakedQuad quad : part.getQuads(direction)) {
                    return new LookupResult(quad.sprite(), quad.tintIndex());
                }
                // Fall back on any other ones, if they're there.
                // This will make cross blocks textures a bit better.
                for (BakedQuad quad : part.getQuads(null)) {
                    // Separate out the sides from top and bottom, because those are often not compatible
                    Direction quadDirection = quad.direction();
                    if (quadDirection.getAxis().isVertical() != direction.getAxis().isVertical()) {
                        continue;
                    }
                    result = new LookupResult(quad.sprite(), quad.tintIndex());
                    // Always prefer a direction match
                    if (quadDirection == direction) {
                        return result;
                    }
                }
            }
            if (result == null) {
                return fallback.lookup(sprites, direction, randomSource);
            }
            return result;
        }
    }
}
