package com.tomboshoven.minecraft.magicdoorknob.modeldata;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
    default LookupResult lookup(Function<? super Material, ? extends TextureAtlasSprite> sprites) {
        return lookup(sprites, Direction.NORTH, new Random());
    }

    /**
     * Perform the actual lookup.
     *
     * @param sprites   The sprite getter to use for looking up texture references.
     * @param direction The direction of the side to get the texture for.
     * @param random    A random source to use in the texture lookup.
     * @return The result of the lookup.
     */
    LookupResult lookup(Function<? super Material, ? extends TextureAtlasSprite> sprites, Direction direction, Random random);

    /**
     * A direct reference to a material.
     *
     * @param material The material referring to the texture to use.
     */
    record MaterialTextureSource(Material material) implements TextureSourceReference {
        @Override
        public LookupResult lookup(Function<? super Material, ? extends TextureAtlasSprite> sprites, Direction direction, Random random) {
            return new LookupResult(sprites.apply(material), null);
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
        public LookupResult lookup(Function<? super Material, ? extends TextureAtlasSprite> sprites, Direction direction, Random random) {
            Minecraft minecraft = Minecraft.getInstance();
            BlockModelShaper blockModelShaper = minecraft.getBlockRenderer().getBlockModelShaper();
            BakedModel blockModel = blockModelShaper.getBlockModel(blockState);
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
