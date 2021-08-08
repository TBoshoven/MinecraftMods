package com.tomboshoven.minecraft.magicmirror.data;

import com.tomboshoven.minecraft.magicmirror.MagicMirrorMod;
import com.tomboshoven.minecraft.magicmirror.blocks.Blocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class Language extends LanguageProvider {
    Language(DataGenerator gen) {
        super(gen, MagicMirrorMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(Blocks.MAGIC_MIRROR_CORE.get(), "Magic Mirror");
        add(Blocks.MAGIC_MIRROR_PART.get(), "Magic Mirror");
        add(Blocks.MAGIC_MIRROR_INACTIVE.get(), "Magic Mirror (inactive)");

        add("commands.magic_mirror.debug.reflections", "Total number of reflections: %d");
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicMirrorMod.MOD_ID, super.getName());
    }
}
