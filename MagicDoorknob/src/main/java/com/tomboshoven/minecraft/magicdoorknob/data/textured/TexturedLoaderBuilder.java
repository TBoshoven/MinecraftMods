package com.tomboshoven.minecraft.magicdoorknob.data.textured;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tomboshoven.minecraft.magicdoorknob.client.modelloaders.ModelLoaders;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Custom model loader builder for textured models.
 */
public class TexturedLoaderBuilder extends CustomLoaderBuilder {
    // The base builder, in case we're applying this to a custom model loader.
    @Nullable
    private CustomLoaderBuilder baseLoaderBuilder;

    public TexturedLoaderBuilder() {
        super(ModelLoaders.TEXTURED_MODEL_LOADER_KEY, true);
    }

    /**
     * Apply a custom model loader to this one.
     *
     * @param loaderFactory Factory to create the base custom loader builder.
     * @param action Any actions to perform on the base loader.
     */
    public <L extends CustomLoaderBuilder> void baseLoader(Supplier<L> loaderFactory, Consumer<? super L> action) {
        L customLoaderBuilder = loaderFactory.get();
        action.accept(customLoaderBuilder);
        baseLoaderBuilder = customLoaderBuilder;
    }

    @Override
    protected CustomLoaderBuilder copyInternal() {
        TexturedLoaderBuilder builder = new TexturedLoaderBuilder();
        builder.baseLoaderBuilder = baseLoaderBuilder;
        return builder;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        if (baseLoaderBuilder != null) {
            baseLoaderBuilder.toJson(json);
        }
        JsonElement loader = json.get("loader");
        if (loader != null) {
            json.add("base_loader", loader);
        }
        return super.toJson(json);
    }
}
