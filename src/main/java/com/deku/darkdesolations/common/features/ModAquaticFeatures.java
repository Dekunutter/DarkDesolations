package com.deku.darkdesolations.common.features;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModAquaticFeatures {
    public static ResourceKey<ConfiguredFeature<?, ?>> DEAD_CORAL_FOREST = registerAquaticFeatureKey("dead_coral_forest");

    /**
     * Registers a resource key for the given aquatic feature name
     *
     * @param featureName Name of the feature we want to create a resource key for
     * @return The resource key created for the given feature
     */
    public static ResourceKey<ConfiguredFeature<?, ?>> registerAquaticFeatureKey(String featureName) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(MOD_ID, featureName));
    }

    /**
     * Creates a feature configuration for the dead coral forest
     *
     * @return Random feature configuration for spreading dead coral features
     */
    private static SimpleRandomFeatureConfiguration createDeadCoralForestConfiguration() {
        return new SimpleRandomFeatureConfiguration(
            HolderSet.direct(
                PlacementUtils.inlinePlaced(ModFeatures.DEAD_CLAW_CORAL_FEATURE, FeatureConfiguration.NONE),
                PlacementUtils.inlinePlaced(ModFeatures.DEAD_TREE_CORAL_FEATURE, FeatureConfiguration.NONE),
                PlacementUtils.inlinePlaced(ModFeatures.DEAD_MUSHROOM_CORAL_FEATURE, FeatureConfiguration.NONE)
            )
        );
    }

    /**
     * Registers aquatic features using the bootstrap context
     *
     * @param context The bootstrap context
     */
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatureGetter = context.lookup(Registries.CONFIGURED_FEATURE);
        HolderGetter<PlacedFeature> placedFeatureGetter = context.lookup(Registries.PLACED_FEATURE);

        context.register(DEAD_CORAL_FOREST, new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, createDeadCoralForestConfiguration()));
    }
}
