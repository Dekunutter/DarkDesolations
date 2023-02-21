package com.deku.darkdesolations.common.world.gen.placements;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModPlacements {
    /**
     * Loads all different placed features for this mod, providing them with a bootstrap context
     *
     * @param context The bootstrap context we'll use to initialize placed features
     */
    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        ModVegetationPlacements.bootstrap(context);
        ModAquaticPlacements.bootstrap(context);
    }
}
