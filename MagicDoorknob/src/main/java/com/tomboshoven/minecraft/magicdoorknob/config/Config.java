package com.tomboshoven.minecraft.magicdoorknob.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

public class Config {
    private static final ModConfigSpec.Builder SERVER_BUILDER;
    public final static Server SERVER;
    private final static ModConfigSpec SERVER_CONFIG;

    static {
        SERVER_BUILDER = new ModConfigSpec.Builder();
        SERVER = new Server(SERVER_BUILDER);
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static class Server {
        public final ModConfigSpec.DoubleValue doorwayMultiplier;

        private Server(ModConfigSpec.Builder builder) {
            // Max value gives a maximum length, using Vanilla materials, of 12 * 8 = 96 blocks
            doorwayMultiplier = builder.comment("Multiplier for the doorway length")
                    .defineInRange("doorwayMultiplier", 1., 0, 8);
        }
    }

    public static void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
    }
}
