package com.tomboshoven.minecraft.magicdoorknob.modelloaders.textured;

import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;

/**
 * Model property pointing to a texture.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModelTextureProperty extends ModelProperty<Material> {
    // Lazily filled map of model texture properties.
    // Can't just use equality as they are used in an IdentityHashMap.
    private static Map<ResourceLocation, ModelTextureProperty> PROPERTIES = Maps.newHashMap();

    private ResourceLocation name;

    /**
     * @param name The name of the property
     */
    private ModelTextureProperty(ResourceLocation name) {
        this.name = name;
    }

    /**
     * Get the model texture property with the given name.
     * It will be created if it hasn't been requested before.
     *
     * @param name The name of the property to get.
     * @return The requested property.
     */
    public static ModelTextureProperty get(ResourceLocation name) {
        return PROPERTIES.computeIfAbsent(name, ModelTextureProperty::new);
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
