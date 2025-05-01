package com.tomboshoven.minecraft.magicdoorknob.modeldata;

import com.google.common.collect.Maps;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jetbrains.annotations.NonNls;

import java.util.Map;
import java.util.Objects;

/**
 * Model property pointing to a texture.
 */
public final class ModelTextureProperty extends ModelProperty<Material> {
    // The namespace of the properties; used in model definitions
    public static final @NonNls String PROPERTY_NAMESPACE = "property";

    // Lazily filled map of model texture properties.
    // Can't just use equality as they are used in an IdentityHashMap.
    private static final Map<ResourceLocation, ModelTextureProperty> PROPERTIES = Maps.newHashMap();

    private final ResourceLocation name;

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

    /**
     * @return The name (resource location) for this texture property.
     */
    public ResourceLocation getName() {
        return name;
    }
}
