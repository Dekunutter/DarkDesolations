package com.deku.darkdesolations.utils;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfiguration {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_SPEC;


    public static final ForgeConfigSpec.ConfigValue<Boolean> spawnCoralForest;

    static {
        BUILDER.comment("------ Dark Desolations General Settings ------").push("dark_desolations");
            BUILDER.push("biomes");
                spawnCoralForest = BUILDER.comment("Whether dead coral forest biomes should spawn").define("coralForest", true);
            BUILDER.pop();
            BUILDER.push("weapons");
            BUILDER.pop();
        BUILDER.pop();

        COMMON_SPEC = BUILDER.build();
    }
}
