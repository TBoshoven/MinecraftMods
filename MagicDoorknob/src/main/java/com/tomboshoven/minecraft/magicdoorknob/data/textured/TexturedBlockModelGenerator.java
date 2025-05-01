package com.tomboshoven.minecraft.magicdoorknob.data.textured;

import com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.textured.TexturedBlockModelDefinition;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.world.level.block.Block;

/**
 * A block model generator for textured models.
 * This uses an existing block model generator as a base.
 */
public class TexturedBlockModelGenerator implements BlockModelDefinitionGenerator {
    /**
     * The generator that is used as a base.
     */
    private final BlockModelDefinitionGenerator baseGenerator;

    /**
     * @param baseGenerator The generator that is used as a base.
     */
    private TexturedBlockModelGenerator(BlockModelDefinitionGenerator baseGenerator) {
        this.baseGenerator = baseGenerator;
    }

    /**
     * @param baseGenerator The generator that is used as a base.
     * @return A textured version of the given block model generator.
     */
    public static TexturedBlockModelGenerator of(BlockModelDefinitionGenerator baseGenerator) {
        return new TexturedBlockModelGenerator(baseGenerator);
    }

    @Override
    public Block block() {
        return baseGenerator.block();
    }

    @Override
    public BlockModelDefinition create() {
        // Custom block model definitions need to be wrapped inside regular block model definitions.
        return new BlockModelDefinition(new TexturedBlockModelDefinition(baseGenerator.create()));
    }
}
