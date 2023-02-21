package com.deku.darkdesolations.common.features;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModVegetationFeatures {
    public static ResourceKey<ConfiguredFeature<?, ?>> LAND_CORAL_FOREST = registerVegetationFeatureKey("land_coral_orest");

    /**
     * Registers a resource key for the given vegetation feature name
     *
     * @param featureName Name of the feature we want to create a resource key for
     * @return The resource key created for the given feature
     */
    public static ResourceKey<ConfiguredFeature<?, ?>> registerVegetationFeatureKey(String featureName) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MOD_ID, featureName));
    }

    /**
     * Creates a feature configuration for the dead coral forest on land
     *
     * @return Random feature configuration for spreading dead coral features on land
     */
    private static SimpleRandomFeatureConfiguration createLandCoralForestConfiguration2() {
        return new SimpleRandomFeatureConfiguration(
            HolderSet.direct(
                PlacementUtils.inlinePlaced(ModFeatures.LAND_CLAW_CORAL_FEATURE, FeatureConfiguration.NONE),
                PlacementUtils.inlinePlaced(ModFeatures.LAND_TREE_CORAL_FEATURE, FeatureConfiguration.NONE),
                PlacementUtils.inlinePlaced(ModFeatures.LAND_MUSHROOM_CORAL_FEATURE, FeatureConfiguration.NONE)
            )
        );
    }

    /**
     * Registers vegetation features using the bootstrap context
     *
     * @param context The bootstrap context
     */
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        context.register(LAND_CORAL_FOREST, new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, createLandCoralForestConfiguration2()));
    }
}
