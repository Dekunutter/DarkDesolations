package com.deku.darkdesolations.common.features;

import net.minecraftforge.registries.ObjectHolder;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModFeatures {
    @ObjectHolder(registryName = "minecraft:worldgen/feature", value = MOD_ID + ":land_claw_coral")
    public static LandClawCoralFeature LAND_CLAW_CORAL_FEATURE;

    @ObjectHolder(registryName = "minecraft:worldgen/feature", value = MOD_ID + ":land_tree_coral")
    public static LandTreeCoralFeature LAND_TREE_CORAL_FEATURE;

    @ObjectHolder(registryName = "minecraft:worldgen/feature", value = MOD_ID + ":land_mushroom_coral")
    public static LandMushroomCoralFeature LAND_MUSHROOM_CORAL_FEATURE;

    @ObjectHolder(registryName = "minecraft:worldgen/feature", value = MOD_ID + ":dead_claw_coral")
    public static DeadClawCoralFeature DEAD_CLAW_CORAL_FEATURE;

    @ObjectHolder(registryName = "minecraft:worldgen/feature", value = MOD_ID + ":dead_tree_coral")
    public static DeadTreeCoralFeature DEAD_TREE_CORAL_FEATURE;

    @ObjectHolder(registryName = "minecraft:worldgen/feature", value = MOD_ID + ":dead_mushroom_coral")
    public static DeadMushroomCoralFeature DEAD_MUSHROOM_CORAL_FEATURE;
}
