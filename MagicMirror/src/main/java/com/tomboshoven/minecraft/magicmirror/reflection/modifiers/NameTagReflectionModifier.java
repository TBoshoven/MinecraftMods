package com.tomboshoven.minecraft.magicmirror.reflection.modifiers;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class NameTagReflectionModifier extends ReflectionModifier {
    Component nameTag;

    public NameTagReflectionModifier(Component nameTag) {
        this.nameTag = nameTag;
    }

    @Nullable
    @Override
    public Component applyNameTag(@Nullable Component nameTag) {
        return this.nameTag;
    }
}
