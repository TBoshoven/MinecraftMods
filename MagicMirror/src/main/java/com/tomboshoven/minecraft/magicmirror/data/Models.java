package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import com.tomboshoven.minecraft.magicmirror.blocks.MagicMirrorInactiveBlock;
import com.tomboshoven.minecraft.magicmirror.items.Items;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import static com.tomboshoven.minecraft.magicmirror.MagicMirrorMod.MOD_ID;

class Models extends ModelProvider {
    /**
     * @return The model template for the bottom part of the mirror.
     */
    private static ExtendedModelTemplate magicMirrorPartTemplate() {
        return ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.BACK)
                .requiredTextureSlot(TextureSlot.FRONT)
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, tvb -> tvb.rotation(0, 315, 0).translation(-1, 1.5f, 0).scale(.4f, .6f, .4f))
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, tvb -> tvb.rotation(0, 315, 0).translation(-1, 1.5f, 0).scale(.4f, .6f, .4f))
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, tvb -> tvb.rotation(0, 245, 30).translation(-5, .5f, -2).scale(.5f, .5f, .5f))
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, tvb -> tvb.rotation(0, 245, 30).translation(-5, .5f, -2).scale(.5f, .5f, .5f))
                .transform(ItemDisplayContext.GUI, tvb -> tvb.rotation(30, 45, 0).translation(3, -2, 0).scale(.625f, .625f, .623f))
                .transform(ItemDisplayContext.GROUND, tvb -> tvb.rotation(0, 0, 0).translation(3, -2, 0).scale(.625f, .625f, .623f))
                .transform(ItemDisplayContext.FIXED, tvb -> tvb.rotation(0, 180, 0).translation(0, 0, -12).scale(1, 1, 1))
                // Back
                .element(elementBuilder -> elementBuilder
                        .from(1, 1, 0).to(15, 16, 1)
                        .face(Direction.NORTH, fb -> fb.texture(TextureSlot.BACK).uvs(1, 0, 15, 15).cullface(Direction.NORTH))
                        .face(Direction.SOUTH, fb -> fb.texture(TextureSlot.FRONT).uvs(1, 0, 15, 15))
                        .face(Direction.UP, fb -> fb.texture(TextureSlot.BACK).uvs(1, 0, 15, 1).cullface(Direction.UP))
                )
                // Bottom
                .element(elementBuilder -> elementBuilder
                        .from(0, 0, 0).to(16, 1, 2)
                        .face(Direction.NORTH, fb -> fb.texture(TextureSlot.BACK).uvs(0, 15, 16, 16).cullface(Direction.NORTH))
                        .face(Direction.EAST, fb -> fb.texture(TextureSlot.BACK).uvs(14, 15, 16, 16).cullface(Direction.EAST))
                        .face(Direction.SOUTH, fb -> fb.texture(TextureSlot.FRONT).uvs(0, 15, 16, 16))
                        .face(Direction.WEST, fb -> fb.texture(TextureSlot.BACK).uvs(0, 15, 2, 16).cullface(Direction.WEST))
                        .face(Direction.UP, fb -> fb.texture(TextureSlot.BACK).uvs(0, 14, 16, 16))
                        .face(Direction.DOWN, fb -> fb.texture(TextureSlot.BACK).uvs(0, 14, 16, 16).cullface(Direction.DOWN))
                )
                // Left
                .element(elementBuilder -> elementBuilder
                        .from(0, 1, 0).to(1, 16, 2)
                        .face(Direction.NORTH, fb -> fb.texture(TextureSlot.BACK).uvs(15, 0, 16, 15).cullface(Direction.NORTH))
                        .face(Direction.EAST, fb -> fb.texture(TextureSlot.BACK).uvs(0, 0, 2, 15))
                        .face(Direction.SOUTH, fb -> fb.texture(TextureSlot.FRONT).uvs(0, 0, 1, 15))
                        .face(Direction.WEST, fb -> fb.texture(TextureSlot.BACK).uvs(0, 0, 2, 15).cullface(Direction.WEST))
                        .face(Direction.UP, fb -> fb.texture(TextureSlot.BACK).uvs(0, 0, 1, 2).cullface(Direction.UP))
                )
                // Right
                .element(elementBuilder -> elementBuilder
                        .from(15, 1, 0).to(16, 16, 2)
                        .face(Direction.NORTH, fb -> fb.texture(TextureSlot.BACK).uvs(0, 0, 1, 15).cullface(Direction.NORTH))
                        .face(Direction.EAST, fb -> fb.texture(TextureSlot.BACK).uvs(14, 0, 16, 15).cullface(Direction.EAST))
                        .face(Direction.SOUTH, fb -> fb.texture(TextureSlot.FRONT).uvs(15, 0, 16, 15))
                        .face(Direction.WEST, fb -> fb.texture(TextureSlot.BACK).uvs(0, 0, 2, 15))
                        .face(Direction.UP, fb -> fb.texture(TextureSlot.BACK).uvs(15, 0, 16, 2).cullface(Direction.UP))
                ).build();
    }

    Models(PackOutput packOutput) {
        super(packOutput, MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        ResourceLocation magicMirrorModelLocation = modLocation("block/magic_mirror");
        ResourceLocation textureFrontLocation = modLocation("block/mirror");
        ResourceLocation textureBackLocation = modLocation("block/mirror_back");

        magicMirrorPartTemplate().create(
                magicMirrorModelLocation,
                new TextureMapping()
                        .put(TextureSlot.FRONT, textureFrontLocation)
                        .put(TextureSlot.BACK, textureBackLocation)
                        .putForced(TextureSlot.PARTICLE, textureFrontLocation),
                blockModels.modelOutput
        );

        // Magic Mirror Core block
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(Blocks.MAGIC_MIRROR_CORE.get(), Variant.variant().with(VariantProperties.MODEL, magicMirrorModelLocation)).with(
                        PropertyDispatch.property(HorizontalDirectionalBlock.FACING)
                                .select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.SOUTH, Variant.variant())
                                .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                )
        );
        // Magic Mirror Inactive block
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(Blocks.MAGIC_MIRROR_INACTIVE.get(), Variant.variant().with(VariantProperties.MODEL, magicMirrorModelLocation)).with(
                        PropertyDispatch.properties(HorizontalDirectionalBlock.FACING, MagicMirrorInactiveBlock.PART)
                                .select(Direction.NORTH, MagicMirrorInactiveBlock.EnumPartType.TOP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.SOUTH, MagicMirrorInactiveBlock.EnumPartType.TOP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.EAST, MagicMirrorInactiveBlock.EnumPartType.TOP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.WEST, MagicMirrorInactiveBlock.EnumPartType.TOP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.NORTH, MagicMirrorInactiveBlock.EnumPartType.BOTTOM, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.SOUTH, MagicMirrorInactiveBlock.EnumPartType.BOTTOM, Variant.variant())
                                .select(Direction.EAST, MagicMirrorInactiveBlock.EnumPartType.BOTTOM, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                .select(Direction.WEST, MagicMirrorInactiveBlock.EnumPartType.BOTTOM, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                )
        );
        // Magic Mirror Part block
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(Blocks.MAGIC_MIRROR_PART.get(), Variant.variant().with(VariantProperties.MODEL, magicMirrorModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).with(
                        PropertyDispatch.property(HorizontalDirectionalBlock.FACING)
                                .select(Direction.NORTH, Variant.variant())
                                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                )
        );
        blockModels.registerSimpleItemModel(Items.MAGIC_MIRROR.get(), magicMirrorModelLocation);
    }
}
