package com.tomboshoven.minecraft.magicdoorknob.data;

import com.tomboshoven.minecraft.magicdoorknob.MagicDoorknobMod;
import com.tomboshoven.minecraft.magicdoorknob.blocks.Blocks;
import com.tomboshoven.minecraft.magicdoorknob.items.Items;
import com.tomboshoven.minecraft.magicdoorknob.items.MagicDoorknobItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class Language extends LanguageProvider {
    Language(DataGenerator gen) {
        super(gen, MagicDoorknobMod.MOD_ID, "en_us");
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
        MagicDoorknobItem doorknob = Items.DOORKNOBS.get(typeName);
        add(doorknob, String.format("%s Magic Doorknob", materialName));
    }

    @Nonnull
    @Override
    public String getName() {
        return String.format("%s %s", MagicDoorknobMod.MOD_ID, super.getName());
    }
}
