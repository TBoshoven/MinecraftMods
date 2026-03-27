package com.tomboshoven.minecraft.magicdoorknob.modeldata;

import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jetbrains.annotations.NonNls;

import java.util.Map;
import java.util.Objects;

/**
 * Model property pointing to a material.
 */
public final class ModelMaterialInfoProperty extends ModelProperty<MaterialInfoSource> {
    // The namespace of the properties; used in model definitions
    public static final @NonNls String PROPERTY_NAMESPACE = "property";

    // Lazily filled map of material info properties.
    // Can't just use equality as they are used in an IdentityHashMap.
    private static final Map<Identifier, ModelMaterialInfoProperty> PROPERTIES = Maps.newHashMap();

    private final Identifier name;

    /**
     * @param name The name of the property
     */
    private ModelMaterialInfoProperty(Identifier name) {
        this.name = name;
    }

    /**
     * Get the model material info property with the given name.
     * It will be created if it hasn't been requested before.
     *
     * @param name The name of the property to get.
     * @return The requested property.
     */
    public static ModelMaterialInfoProperty get(Identifier name) {
        return PROPERTIES.computeIfAbsent(name, ModelMaterialInfoProperty::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelMaterialInfoProperty that = (ModelMaterialInfoProperty) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * @return The name (resource location) for this property.
     */
    public Identifier getName() {
        return name;
    }
}
