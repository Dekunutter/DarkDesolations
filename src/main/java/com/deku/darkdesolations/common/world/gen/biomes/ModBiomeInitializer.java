package com.deku.darkdesolations.common.world.gen.biomes;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModBiomeInitializer {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MOD_ID);

    public static ResourceKey<Biome> CORAL_DESERT = registerBiomeKey("coral_desert");

    /**
     * Registers the biome into the vanilla game by the biome registry
     *
     * @param biomeName The registry name of the biome
     * @return The registered key for the custom biome
     */
    public static ResourceKey<Biome> registerBiomeKey(String biomeName) {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(MOD_ID, biomeName));
    }

    /**
     * Bootstraps the context needed to register the biomes for the mod
     *
     * @param context Bootstrap context needed to register biomes to the game
     */
    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placementGetter = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverGetter = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(CORAL_DESERT, OverworldBiomes.theVoid(placementGetter, carverGetter));
    }

    /**
     * Registers all custom biomes into the overworld so that they actually show up during world generation.
     * The weight determines the commonality of the biome.
     */
    public static void registerBiomes() {
        BiomeManager.addBiome(BiomeManager.BiomeType.DESERT, new BiomeManager.BiomeEntry(CORAL_DESERT, 1));
    }
}
