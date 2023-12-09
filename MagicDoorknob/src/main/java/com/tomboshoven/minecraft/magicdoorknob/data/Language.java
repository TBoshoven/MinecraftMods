package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

class Language extends LanguageProvider {
    Language(PackOutput output) {
        super(output, MagicDoorknobMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(Blocks.MAGIC_DOOR.get(), "Magic Door");
        add(Blocks.MAGIC_DOORWAY.get(), "Magic Doorway");

        addDoorknob("diamond");
        addDoorknob("gold", "Golden");
        addDoorknob("iron");
        addDoorknob("netherite");
        addDoorknob("stone");
        addDoorknob("wood", "Wooden");
    }

    private void addDoorknob(String typeName) {
        addDoorknob(typeName, StringUtils.capitalize(typeName));
    }

    private void addDoorknob(String typeName, String materialName) {
        MagicDoorknobItem doorknob = Items.DOORKNOBS.get(typeName).get();
        add(doorknob, String.format("%s Magic Doorknob", materialName));
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicDoorknobMod.MOD_ID, super.getName());
    }
}
