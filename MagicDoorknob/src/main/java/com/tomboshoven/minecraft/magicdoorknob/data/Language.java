package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;

public class Language extends LanguageProvider {
    public Language(DataGenerator gen) {
        super(gen, MagicDoorknobMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(Blocks.MAGIC_DOOR, "Magic Door");
        add(Blocks.MAGIC_DOORWAY, "Magic Doorway");

        addDoorknob("diamond");
        addDoorknob("gold", "Golden");
        addDoorknob("iron");
        addDoorknob("stone");
        addDoorknob("wood", "Wooden");
    }

    protected void addDoorknob(String typeName) {
        addDoorknob(typeName, StringUtils.capitalize(typeName));
    }

    protected void addDoorknob(String typeName, String materialName) {
        MagicDoorknobItem doorknob = Items.DOORKNOBS.get(typeName);
        add(doorknob, String.format("%s Magic Doorknob", materialName));
    }
}
