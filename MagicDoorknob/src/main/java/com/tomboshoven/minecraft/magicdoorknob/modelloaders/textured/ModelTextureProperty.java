package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * Model property pointing to a texture.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModelTextureProperty extends ModelProperty<ResourceLocation> {
    private ResourceLocation name;

    /**
     * @param name The name of the property
     */
    public ModelTextureProperty(ResourceLocation name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelTextureProperty that = (ModelTextureProperty) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
