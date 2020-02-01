package com.tomboshoven.minecraft.magicdoorknob.properties;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * A (unlisted) property type containing texture information.
 */
public class PropertyTexture implements IUnlistedProperty<ResourceLocation> {
    /**
     * Name of the property.
     */
    private final String name;

    /**
     * @param name The name of the property.
     */
    public PropertyTexture(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(ResourceLocation value) {
        return true;
    }

    @Override
    public Class<ResourceLocation> getType() {
        return ResourceLocation.class;
    }

    @Override
    public String valueToString(ResourceLocation value) {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PropertyTexture && name.equals(((PropertyTexture) o).name);
    }
}
