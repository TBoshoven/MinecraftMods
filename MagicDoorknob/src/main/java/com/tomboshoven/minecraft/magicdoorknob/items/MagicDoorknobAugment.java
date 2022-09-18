package com.tomboshoven.minecraft.magicdoorknob.items;

public enum MagicDoorknobAugment {
    EXTRA_DOORKNOB("extra_doorknob");

    String id;

    MagicDoorknobAugment(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
