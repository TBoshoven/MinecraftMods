package com.tomboshoven.minecraft.magicdoorknob.data;

import com.mojang.math.Transformation;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.MagicDoorwayPartBaseBlock;
import com.tomboshoven.minecraft.magicdoorknob.blocks.entities.MagicDoorwayPartBaseBlockEntity;
import com.tomboshoven.minecraft.magicdoorknob.data.textured.TexturedLoaderBuilder;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.template.ElementBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.client.model.generators.template.FaceBuilder;
import net.neoforged.neoforge.client.model.generators.template.FaceRotation;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod.MOD_ID;

class Models extends ModelProvider {
    private static final TextureSlot MAIN_TEXTURE = TextureSlot.create("main");
    private static final TextureSlot HIGHLIGHT_TEXTURE = TextureSlot.create("highlight");

    /**
     * Rotate UV coordinates based on a given face rotation and apply them to the FaceBuilder.
     *
     * @param faceBuilder The face builder to set the UVs on.
     * @param baseFace    The face to base the reorientation on.
     * @param u1          The horizontal start coordinate from the left of the texture.
     * @param v1          The vertical start coordinate from the top of the texture.
     * @param u2          The horizontal end coordinate from the left of the texture.
     * @param v2          The vertical end coordinate from the top of the texture.
     * @param rotation    The rotation of the texture.
     */
    private static FaceBuilder orientedUVs(FaceBuilder faceBuilder, Direction baseFace, int u1, int v1, int u2, int v2, FaceRotation rotation) {
        // Reuse logic from facebakery, because there is no point in reinventing the wheel.
        // This is probably not the most efficient, but it's fine for model gen.
        BlockFaceUV faceUV = FaceBakery.recomputeUVs(new BlockFaceUV(new float[]{u1, v1, u2, v2}, switch (rotation) {
            case ZERO -> 0;
            case CLOCKWISE_90 -> 90;
            case UPSIDE_DOWN -> 180;
            case COUNTERCLOCKWISE_90 -> 270;
        }), baseFace, Transformation.identity());
        // Round the resulting UVs because of floating point errors. We're sure these are all integers in the end.
        return faceBuilder
                .uvs(Math.round(faceUV.uvs[0]), Math.round(faceUV.uvs[1]), Math.round(faceUV.uvs[2]), Math.round(faceUV.uvs[3]))
                .rotation(switch (faceUV.rotation) {
                    case 90 -> FaceRotation.CLOCKWISE_90;
                    case 180 -> FaceRotation.UPSIDE_DOWN;
                    case 270 -> FaceRotation.COUNTERCLOCKWISE_90;
                    default -> FaceRotation.ZERO;
                });
    }

    /**
     * Create a cuboid that is part of a 1-thick textured shape on a wall.
     * Coordinates are provided in texture coordinates, so 0-16, top to bottom.
     * Note that this is different from model coordinates, which go from bottom to top.
     *
     * @param wall       The side of the block that the panel is placed against.
     * @param x1         The x coordinate to start on.
     * @param y1         The y coordinate to start on.
     * @param x2         The x coordinate to end on.
     * @param y2         The y coordinate to end on.
     * @param faceAction The face action to apply to all faces. Used for setting textures.
     * @param renderEdge A function that specifies for each direction whether to render the edge.
     *                   This is specified pre-rotation, as if the cuboid was on the north wall.
     */
    private static Consumer<ElementBuilder> wallCuboid(Direction wall, int x1, int y1, int x2, int y2, BiConsumer<Direction, FaceBuilder> faceAction, Function<? super Direction, Boolean> renderEdge) {
        return elementBuilder -> {
            switch (wall) {
                case DOWN -> elementBuilder.from(x1, 0, 16 - y2).to(x2, 1, 16 - y1);
                case UP -> elementBuilder.from(x1, 15, y1).to(x2, 16, y2);
                case NORTH -> elementBuilder.from(16 - x2, 16 - y2, 0).to(16 - x1, 16 - y1, 1);
                case SOUTH -> elementBuilder.from(x1, 16 - y2, 15).to(x2, 16 - y1, 16);
                case WEST -> elementBuilder.from(0, 16 - y2, x1).to(1, 16 - y1, x2);
                case EAST -> elementBuilder.from(15, 16 - y2, 16 - x2).to(16, 16 - y1, 16 - x1);
            }

            // Define some directions
            Direction top = switch (wall) {
                case DOWN -> Direction.SOUTH;
                case UP -> Direction.NORTH;
                case NORTH, SOUTH, WEST, EAST -> Direction.UP;
            };
            Direction bottom = top.getOpposite();
            Direction left = switch (wall) {
                case DOWN, UP -> Direction.WEST;
                case NORTH, SOUTH, WEST, EAST -> wall.getClockWise();
            };
            Direction right = left.getOpposite();

            // Mirror horizontally on two of the sides to get connected textures along edges
            boolean mirror = wall.getAxis() == Direction.Axis.Z;

            // Front
            if (renderEdge.apply(Direction.NORTH)) {
                elementBuilder.face(wall, faceBuilder -> orientedUVs(faceBuilder, wall, mirror ? x1 : 16 - x1, y1, mirror ? x2 : 16 - x2, y2, FaceRotation.ZERO).cullface(wall));
            }

            // Back
            if (renderEdge.apply(Direction.SOUTH)) {
                FaceRotation rotation = switch (wall) {
                    case UP, DOWN -> FaceRotation.UPSIDE_DOWN;
                    case NORTH, SOUTH, WEST, EAST -> FaceRotation.ZERO;
                };
                elementBuilder.face(wall.getOpposite(), faceBuilder -> orientedUVs(faceBuilder, wall, mirror ? x2 : 16 - x2, y1, mirror ? x1 : 16 - x1, y2, rotation));
            }

            // Top
            if (renderEdge.apply(Direction.UP)) {
                FaceRotation rotation = switch (wall) {
                    case DOWN, SOUTH -> FaceRotation.ZERO;
                    case UP, NORTH -> FaceRotation.UPSIDE_DOWN;
                    case WEST -> FaceRotation.COUNTERCLOCKWISE_90;
                    case EAST -> FaceRotation.CLOCKWISE_90;
                };
                elementBuilder.face(top, faceBuilder -> {
                    FaceBuilder fb = orientedUVs(faceBuilder, wall, mirror ? x1 : 16 - x1, y1, mirror ? x2 : 16 - x2, y1 + 1, rotation);
                    if (y1 == 0) {
                        fb.cullface(top);
                    }
                });
            }

            // Bottom
            if (renderEdge.apply(Direction.DOWN)) {
                FaceRotation rotation = switch (wall) {
                    case UP, SOUTH -> FaceRotation.ZERO;
                    case DOWN, NORTH -> FaceRotation.UPSIDE_DOWN;
                    case WEST -> FaceRotation.CLOCKWISE_90;
                    case EAST -> FaceRotation.COUNTERCLOCKWISE_90;
                };
                elementBuilder.face(bottom, faceBuilder -> {
                    FaceBuilder fb = orientedUVs(faceBuilder, wall, mirror ? x1 : 16 - x1, y2, mirror ? x2 : 16 - x2, y2 - 1, rotation);
                    if (y2 == 16) {
                        fb.cullface(bottom);
                    }
                });
            }

            // Left
            if (renderEdge.apply(Direction.WEST)) {
                FaceRotation rotation = switch (wall) {
                    case NORTH, SOUTH, WEST, EAST -> FaceRotation.ZERO;
                    case UP -> FaceRotation.CLOCKWISE_90;
                    case DOWN -> FaceRotation.COUNTERCLOCKWISE_90;
                };
                elementBuilder.face(left, faceBuilder -> {
                    FaceBuilder fb = orientedUVs(faceBuilder, wall, mirror ? x1 : 16 - x1, y1, mirror ? x1 + 1 : 16 - x1 - 1, y2, rotation);
                    if (x1 == 0) {
                        fb.cullface(left);
                    }
                });
            }

            // Right
            if (renderEdge.apply(Direction.EAST)) {
                FaceRotation rotation = switch (wall) {
                    case NORTH, SOUTH, WEST, EAST -> FaceRotation.ZERO;
                    case UP -> FaceRotation.COUNTERCLOCKWISE_90;
                    case DOWN -> FaceRotation.CLOCKWISE_90;
                };
                elementBuilder.face(right, faceBuilder -> {
                    FaceBuilder fb = orientedUVs(faceBuilder, wall, mirror ? x2 : 16 - x2, y1, mirror ? x2 - 1 : 16 - x2 + 1, y2, rotation);
                    if (x2 != 16) {
                        fb.cullface(right);
                    }
                });
            }

            elementBuilder.faces(faceAction);
        };
    }

    /**
     * Create a cuboid that is part of a 1-thick textured panel filling a wall.
     * Coordinates are provided in texture coordinates, so 0-16, top to bottom.
     * Note that this is different from model coordinates, which go from bottom to top.
     *
     * @param wall       The side of the block that the panel is placed against.
     * @param x1         The x coordinate to start on.
     * @param y1         The y coordinate to start on.
     * @param x2         The x coordinate to end on.
     * @param y2         The y coordinate to end on.
     * @param faceAction The face action to apply to all faces. Used for setting textures.
     * @param renderEdge A function that specifies for each direction whether to render the edge.
     *                   This is specified pre-rotation, as if the panel was on the north wall.
     */
    private static Consumer<ElementBuilder> panelCuboid(Direction wall, int x1, int y1, int x2, int y2, BiConsumer<Direction, FaceBuilder> faceAction, Function<? super Direction, Boolean> renderEdge) {
        return wallCuboid(wall, x1, y1, x2, y2, faceAction, d -> renderEdge.apply(d) && switch (d) {
            case NORTH, SOUTH -> true;
            case UP -> y1 == 0;
            case DOWN -> y2 == 16;
            case WEST -> x1 == 0;
            case EAST -> x2 == 16;
        });
    }

    /**
     * Create a cuboid that is part of a 1-thick textured panel filling a wall.
     * Coordinates are provided in texture coordinates, so 0-16, top to bottom.
     * Note that this is different from model coordinates, which go from bottom to top.
     *
     * @param wall       The side of the block that the panel is placed against.
     * @param x1         The x coordinate to start on.
     * @param y1         The y coordinate to start on.
     * @param x2         The x coordinate to end on.
     * @param y2         The y coordinate to end on.
     * @param faceAction The face action to apply to all faces. Used for setting textures.
     */
    private static Consumer<ElementBuilder> panelCuboid(Direction wall, int x1, int y1, int x2, int y2, BiConsumer<Direction, FaceBuilder> faceAction) {
        return panelCuboid(wall, x1, y1, x2, y2, faceAction, d -> true);
    }

    /**
     * Create a cuboid that is part of a 1-thick textured pillar touching a wall on the left.
     * Coordinates are provided in texture coordinates, so 0-16, top to bottom.
     * Note that this is different from model coordinates, which go from bottom to top.
     *
     * @param wall       The side of the block that the panel is placed against.
     * @param y1         The y coordinate to start on.
     * @param y2         The y coordinate to end on.
     * @param faceAction The face action to apply to all faces. Used for setting textures.
     * @param renderEdge A function that specifies for each direction whether to render the edge.
     *                   This is specified pre-rotation, as if the pillar was in the north-west corner.
     */
    private static Consumer<ElementBuilder> pillarCuboid(Direction wall, int y1, int y2, BiConsumer<Direction, FaceBuilder> faceAction, Function<? super Direction, Boolean> renderEdge) {
        return wallCuboid(wall, 0, y1, 1, y2, faceAction, d -> renderEdge.apply(d) && switch (d) {
            case NORTH, SOUTH, WEST, EAST -> true;
            case UP -> y1 == 0;
            case DOWN -> y2 == 16;
        });
    }

    /**
     * Create a cuboid that is part of a 1-thick textured pillar touching a wall on the left.
     * Coordinates are provided in texture coordinates, so 0-16, top to bottom.
     * Note that this is different from model coordinates, which go from bottom to top.
     *
     * @param wall       The side of the block that the panel is placed against.
     * @param y1         The y coordinate to start on.
     * @param y2         The y coordinate to end on.
     * @param faceAction The face action to apply to all faces. Used for setting textures.
     */
    private static Consumer<ElementBuilder> pillarCuboid(Direction wall, int y1, int y2, BiConsumer<Direction, FaceBuilder> faceAction) {
        return pillarCuboid(wall, y1, y2, faceAction, d -> true);
    }

    /**
     * Create a cuboid that is textured on the north and south faces (mirrored) and stretched along the other faces.
     * Mainly useful for 1-thick cuboids or intersected ones.
     */
    private static Consumer<ElementBuilder> stretchCuboid(float x1, float y1, float z1, float x2, float y2, float z2, TextureSlot texture) {
        return elementBuilder -> elementBuilder
                .from(x1, y1, z1)
                .to(x2, y2, z2)
                .textureAll(texture)
                .face(Direction.NORTH, builder -> builder.uvs(x2, y1, x1, y2))
                .face(Direction.SOUTH, builder -> builder.uvs(x1, y1, x2, y2))
                .face(Direction.EAST, builder -> builder.uvs(x2, y1, x2, y2))
                .face(Direction.WEST, builder -> builder.uvs(x1, y1, x1, y2))
                .face(Direction.UP, builder -> builder.uvs(x1, y1, x2, y1))
                .face(Direction.DOWN, builder -> builder.uvs(x1, y2, x2, y2));
    }

    /**
     * @param blockModelLocation The location of the default block model.
     * @return The model template for the untextured doorknob model.
     */
    private static ExtendedModelTemplate doorknobBaseTemplate(ResourceLocation blockModelLocation) {
        return ExtendedModelTemplateBuilder.builder()
                .parent(blockModelLocation)
                // Body
                .element(stretchCuboid(3, 3, 7, 13, 13, 10, MAIN_TEXTURE))
                // Tall
                .element(stretchCuboid(3, 2, 8, 13, 14, 9, MAIN_TEXTURE))
                // Wide
                .element(stretchCuboid(2, 3, 8, 14, 13, 9, MAIN_TEXTURE))
                // Front
                .element(stretchCuboid(4, 4, 6, 12, 12, 7, MAIN_TEXTURE))
                // Stalk
                .element(stretchCuboid(6, 6, 10, 10, 10, 15, MAIN_TEXTURE))
                .build();
    }

    /**
     * @param doorKnobBaseModelLocation The model location for the base (untextured) doorknob.
     * @return The model template for textured doorknob model.
     */
    private static ExtendedModelTemplate doorknobTemplate(ResourceLocation doorKnobBaseModelLocation) {
        return ExtendedModelTemplateBuilder.builder().parent(doorKnobBaseModelLocation).requiredTextureSlot(MAIN_TEXTURE).build();
    }

    /**
     * @param partType The part of the door to generate the model for.
     * @return The model template for the door part.
     */
    private static ExtendedModelTemplate doorTemplate(MagicDoorwayPartBaseBlock.EnumPartType partType) {
        // For the top panel, we don't render the bottom edge; for the bottom panel, we don't render the top.
        // This only holds for doors; doorways can be partially broken.
        Function<Direction, Boolean> renderEdge = switch (partType) {
            case TOP -> d -> d != Direction.DOWN;
            case BOTTOM -> d -> d != Direction.UP;
        };
        BiConsumer<Direction, FaceBuilder> mainTextureAction = (d, faceBuilder) -> faceBuilder.texture(MAIN_TEXTURE).tintindex(1);
        BiConsumer<Direction, FaceBuilder> highlightTextureAction = (d, faceBuilder) -> faceBuilder.texture(HIGHLIGHT_TEXTURE);
        // Note: door facing is outward, so we need to face east, not west
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .customLoader(TexturedLoaderBuilder::new, loader -> {
                })
                .parent(ResourceLocation.withDefaultNamespace("block/block"))
                .renderType("translucent")
                .requiredTextureSlot(MAIN_TEXTURE)
                .requiredTextureSlot(HIGHLIGHT_TEXTURE)
                // Top left
                .element(panelCuboid(Direction.EAST, 0, 0, 1, 7, mainTextureAction, renderEdge))
                // Top left highlight
                .element(panelCuboid(Direction.EAST, 1, 0, 2, 7, highlightTextureAction, renderEdge))
                // Top center
                .element(panelCuboid(Direction.EAST, 2, 0, 3, 7, mainTextureAction, renderEdge))
                // Top right highlight
                .element(panelCuboid(Direction.EAST, 3, 0, 4, 7, highlightTextureAction, renderEdge))
                // Top right
                .element(panelCuboid(Direction.EAST, 4, 0, 16, 7, mainTextureAction, renderEdge))
                // Top highlight
                .element(panelCuboid(Direction.EAST, 0, 7, 16, 8, highlightTextureAction))
                // Middle left
                .element(panelCuboid(Direction.EAST, 0, 8, 1, 9, mainTextureAction))
                // Middle left highlight
                .element(panelCuboid(Direction.EAST, 1, 8, 2, 9, highlightTextureAction))
                // Middle center
                .element(panelCuboid(Direction.EAST, 2, 8, 3, 9, mainTextureAction))
                // Middle right highlight
                .element(panelCuboid(Direction.EAST, 3, 8, 4, 9, highlightTextureAction))
                // Middle right
                .element(panelCuboid(Direction.EAST, 4, 8, 16, 9, mainTextureAction))
                // Bottom highlight
                .element(panelCuboid(Direction.EAST, 0, 9, 16, 10, highlightTextureAction))
                // Bottom left
                .element(panelCuboid(Direction.EAST, 0, 10, 1, 16, mainTextureAction, renderEdge))
                // Bottom left highlight
                .element(panelCuboid(Direction.EAST, 1, 10, 2, 16, highlightTextureAction, renderEdge))
                // Bottom center
                .element(panelCuboid(Direction.EAST, 2, 10, 3, 16, mainTextureAction, renderEdge))
                // Bottom right highlight
                .element(panelCuboid(Direction.EAST, 3, 10, 4, 16, highlightTextureAction, renderEdge))
                // Bottom right
                .element(panelCuboid(Direction.EAST, 4, 10, 16, 16, mainTextureAction, renderEdge));
        if (partType == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
            // Add the doorknob
            // All faces are culled to the east to prevent the knob from poking through blocks
            builder
                    // Knob
                    .element(eb -> eb
                            .from(17, -1, 1)
                            .to(18, 2, 4)
                            .face(Direction.NORTH, fb -> fb.uvs(16, 0, 0, 16))
                            .face(Direction.SOUTH, fb -> fb.uvs(16, 0, 0, 16))
                            .face(Direction.UP, fb -> fb.uvs(16, 0, 0, 16).rotation(FaceRotation.CLOCKWISE_90))
                            .face(Direction.DOWN, fb -> fb.uvs(16, 0, 0, 16).rotation(FaceRotation.COUNTERCLOCKWISE_90))
                            .face(Direction.EAST, fb -> fb.uvs(0, 0, 16, 16))
                            .face(Direction.WEST, fb -> fb.uvs(0, 0, 16, 16))
                            .faces((d, fb) -> fb.cullface(Direction.EAST))
                            .texture(HIGHLIGHT_TEXTURE)
                    )
                    // Stalk
                    .element(eb -> eb
                            .from(16, 0, 2)
                            .to(17, 1, 3)
                            .face(Direction.UP, fb -> fb.uvs(16f / 3, 16f / 3, 32f / 3, 32f / 3).rotation(FaceRotation.CLOCKWISE_90))
                            .face(Direction.DOWN, fb -> fb.uvs(16f / 3, 16f / 3, 32f / 3, 32f / 3).rotation(FaceRotation.COUNTERCLOCKWISE_90))
                            .face(Direction.NORTH, fb -> fb.uvs(16f / 3, 16f / 3, 32f / 3, 32f / 3))
                            .face(Direction.SOUTH, fb -> fb.uvs(16f / 3, 16f / 3, 32f / 3, 32f / 3))
                            .faces((d, fb) -> fb.cullface(Direction.EAST))
                            .texture(HIGHLIGHT_TEXTURE)
                    );
        }
        return builder.build();
    }

    /**
     * Closed doorways are not currently obtainable, but we add them for completeness.
     *
     * @param partType The part of the doorway to generate the model for.
     * @return The model template for part of a fully closed doorway.
     */
    private static ExtendedModelTemplate closedDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType partType) {
        BiConsumer<Direction, FaceBuilder> mainTextureAction = (d, faceBuilder) -> faceBuilder.texture(MAIN_TEXTURE).tintindex(1);
        BiConsumer<Direction, FaceBuilder> highlightTextureAction = (d, faceBuilder) -> faceBuilder.texture(HIGHLIGHT_TEXTURE);
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .customLoader(TexturedLoaderBuilder::new, loader -> {
                })
                .parent(ResourceLocation.withDefaultNamespace("block/block"))
                .renderType("translucent")
                .requiredTextureSlot(MAIN_TEXTURE)
                .requiredTextureSlot(HIGHLIGHT_TEXTURE);

        // The top panel has a ceiling, so start the panels 1 y-level lower
        int startY;

        if (partType == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
            startY = 1;
            // Add the ceiling
            builder.element(panelCuboid(Direction.UP, 0, 0, 16, 16, mainTextureAction));
        } else {
            startY = 0;
        }

        Direction.stream().filter(d -> d.getAxis() != Direction.Axis.Y).forEach(wall -> {
                    int startX = wall.getAxis() == Direction.Axis.Z ? 1 : 0;
                    int endX = wall.getAxis() == Direction.Axis.Z ? 15 : 16;
                    builder
                            // Top
                            .element(panelCuboid(wall, startX, startY, endX, 7, mainTextureAction))
                            // Top highlight
                            .element(panelCuboid(wall, startX, 7, endX, 8, highlightTextureAction))
                            // Middle
                            .element(panelCuboid(wall, startX, 8, endX, 9, mainTextureAction))
                            // Bottom highlight
                            .element(panelCuboid(wall, startX, 9, endX, 10, highlightTextureAction))
                            // Bottom
                            .element(panelCuboid(wall, startX, 10, endX, 16, mainTextureAction));
                }
        );
        return builder.build();
    }

    /**
     * @param partType The part of the doorway to generate the model for.
     * @return The model template for part of a half-open (north-south) doorway.
     */
    private static ExtendedModelTemplate halfOpenDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType partType) {
        BiConsumer<Direction, FaceBuilder> mainTextureAction = (d, faceBuilder) -> faceBuilder.texture(MAIN_TEXTURE).tintindex(1);
        BiConsumer<Direction, FaceBuilder> highlightTextureAction = (d, faceBuilder) -> faceBuilder.texture(HIGHLIGHT_TEXTURE);
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .customLoader(TexturedLoaderBuilder::new, loader -> {
                })
                .parent(ResourceLocation.withDefaultNamespace("block/block"))
                .renderType("translucent")
                .requiredTextureSlot(MAIN_TEXTURE)
                .requiredTextureSlot(HIGHLIGHT_TEXTURE);

        // The top panel has a ceiling, so start the panels 1 y-level lower
        int startY;

        if (partType == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
            startY = 1;
            // Add the ceiling
            builder.element(panelCuboid(Direction.UP, 0, 0, 16, 16, mainTextureAction));
        } else {
            startY = 0;
        }

        Direction.stream().filter(d -> d.getAxis() == Direction.Axis.Z).forEach(wall ->
                builder
                        // Top
                        .element(panelCuboid(wall, 0, startY, 16, 7, mainTextureAction))
                        // Top highlight
                        .element(panelCuboid(wall, 0, 7, 16, 8, highlightTextureAction))
                        // Middle
                        .element(panelCuboid(wall, 0, 8, 16, 9, mainTextureAction))
                        // Bottom highlight
                        .element(panelCuboid(wall, 0, 9, 16, 10, highlightTextureAction))
                        // Bottom
                        .element(panelCuboid(wall, 0, 10, 16, 16, mainTextureAction))
        );
        return builder.build();
    }

    /**
     * @param partType The part of the doorway to generate the model for.
     * @return The model template for part of a fully open doorway.
     */
    private static ExtendedModelTemplate openDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType partType) {
        BiConsumer<Direction, FaceBuilder> mainTextureAction = (d, faceBuilder) -> faceBuilder.texture(MAIN_TEXTURE).tintindex(1);
        BiConsumer<Direction, FaceBuilder> highlightTextureAction = (d, faceBuilder) -> faceBuilder.texture(HIGHLIGHT_TEXTURE);
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .customLoader(TexturedLoaderBuilder::new, loader -> {
                })
                .parent(ResourceLocation.withDefaultNamespace("block/block"))
                .renderType("translucent")
                .requiredTextureSlot(MAIN_TEXTURE)
                .requiredTextureSlot(HIGHLIGHT_TEXTURE);

        // The top panel has a ceiling, so start the panels 1 y-level lower
        int startY;

        if (partType == MagicDoorwayPartBaseBlock.EnumPartType.TOP) {
            startY = 1;
            // Add the ceiling
            builder.element(panelCuboid(Direction.UP, 0, 0, 16, 16, mainTextureAction));
        } else {
            startY = 0;
        }

        Direction.stream().filter(d -> d.getAxis() != Direction.Axis.Y).forEach(wall ->
                builder
                        // Top
                        .element(pillarCuboid(wall, startY, 7, mainTextureAction))
                        // Top highlight
                        .element(pillarCuboid(wall, 7, 8, highlightTextureAction))
                        // Middle
                        .element(pillarCuboid(wall, 8, 9, mainTextureAction))
                        // Bottom highlight
                        .element(pillarCuboid(wall, 9, 10, highlightTextureAction))
                        // Bottom
                        .element(pillarCuboid(wall, 10, 16, mainTextureAction))
        );
        return builder.build();
    }

    Models(PackOutput packOutput) {
        super(packOutput, MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Model locations
        ResourceLocation doorknobBaseModelLocation = modLocation("item/magic_doorknob");
        ResourceLocation doorTopModelLocation = modLocation("block/magic_door_top");
        ResourceLocation doorBottomModelLocation = modLocation("block/magic_door_bottom");
        ResourceLocation doorwayClosedTopModelLocation = modLocation("block/magic_doorway_closed_top");
        ResourceLocation doorwayClosedBottomModelLocation = modLocation("block/magic_doorway_closed_bottom");
        ResourceLocation doorwayHalfOpenTopModelLocation = modLocation("block/magic_doorway_half_open_top");
        ResourceLocation doorwayHalfOpenBottomModelLocation = modLocation("block/magic_doorway_half_open_bottom");
        ResourceLocation doorwayOpenTopModelLocation = modLocation("block/magic_doorway_open_top");
        ResourceLocation doorwayOpenBottomModelLocation = modLocation("block/magic_doorway_open_bottom");

        // Doorknob items
        doorknobBaseTemplate(mcLocation("block/block")).create(doorknobBaseModelLocation, new TextureMapping(), itemModels.modelOutput);
        ExtendedModelTemplate doorknobTemplate = doorknobTemplate(doorknobBaseModelLocation);
        for (DeferredItem<MagicDoorknobItem> item : Items.DOORKNOBS.values()) {
            MagicDoorknobItem doorknobItem = item.get();
            ResourceLocation modelLocation = doorknobTemplate.create(
                    doorknobItem,
                    new TextureMapping().put(MAIN_TEXTURE, doorknobItem.getMainMaterial().texture()),
                    itemModels.modelOutput
            );
            itemModels.itemModelOutput.accept(doorknobItem, new BlockModelWrapper.Unbaked(
                    modelLocation,
                    Collections.emptyList()
            ));
        }

        // Door blocks
        TextureMapping panelTextureMapping = new TextureMapping()
                .put(MAIN_TEXTURE, MagicDoorwayPartBaseBlockEntity.TEXTURE_MAIN.getName())
                .put(HIGHLIGHT_TEXTURE, MagicDoorwayPartBaseBlockEntity.TEXTURE_HIGHLIGHT.getName());
        doorTemplate(MagicDoorwayPartBaseBlock.EnumPartType.TOP).create(doorTopModelLocation, panelTextureMapping, blockModels.modelOutput);
        doorTemplate(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM).create(doorBottomModelLocation, panelTextureMapping, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(Blocks.MAGIC_DOOR.get())
                        .with(BlockModelGenerators.createHorizontalFacingDispatch())
                        .with(PropertyDispatch.property(MagicDoorBlock.PART)
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, doorBottomModelLocation))
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.TOP, Variant.variant().with(VariantProperties.MODEL, doorTopModelLocation))
                        )
        );

        // Doorway blocks
        closedDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType.TOP).create(doorwayClosedTopModelLocation, panelTextureMapping, blockModels.modelOutput);
        closedDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM).create(doorwayClosedBottomModelLocation, panelTextureMapping, blockModels.modelOutput);
        halfOpenDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType.TOP).create(doorwayHalfOpenTopModelLocation, panelTextureMapping, blockModels.modelOutput);
        halfOpenDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM).create(doorwayHalfOpenBottomModelLocation, panelTextureMapping, blockModels.modelOutput);
        openDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType.TOP).create(doorwayOpenTopModelLocation, panelTextureMapping, blockModels.modelOutput);
        openDoorwayTemplate(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM).create(doorwayOpenBottomModelLocation, panelTextureMapping, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(Blocks.MAGIC_DOORWAY.get())
                        .with(PropertyDispatch.properties(MagicDoorwayBlock.PART, MagicDoorwayBlock.OPEN_NORTH_SOUTH, MagicDoorwayBlock.OPEN_EAST_WEST)
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM, false, false, Variant.variant().with(VariantProperties.MODEL, doorwayClosedBottomModelLocation))
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.TOP, false, false, Variant.variant().with(VariantProperties.MODEL, doorwayClosedTopModelLocation))
                                // Half-open
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM, false, true, Variant.variant().with(VariantProperties.MODEL, doorwayHalfOpenBottomModelLocation))
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.TOP, false, true, Variant.variant().with(VariantProperties.MODEL, doorwayHalfOpenTopModelLocation))
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM, true, false, Variant.variant().with(VariantProperties.MODEL, doorwayHalfOpenBottomModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.TOP, true, false, Variant.variant().with(VariantProperties.MODEL, doorwayHalfOpenTopModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                // Fully open
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.BOTTOM, true, true, Variant.variant().with(VariantProperties.MODEL, doorwayOpenBottomModelLocation))
                                .select(MagicDoorwayPartBaseBlock.EnumPartType.TOP, true, true, Variant.variant().with(VariantProperties.MODEL, doorwayOpenTopModelLocation))
                        )
        );
    }
}
