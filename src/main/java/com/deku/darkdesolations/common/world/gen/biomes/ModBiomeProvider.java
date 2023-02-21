package com.deku.darkdesolations.common.world.gen.biomes;

import com.deku.darkdesolations.utils.ModConfiguration;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModBiomeProvider extends Region {
    public ModBiomeProvider() {
        // Adding low region weight since we only have one biome. Don't want it to be too common
        super(new ResourceLocation(MOD_ID, "desolation"), RegionType.OVERWORLD, 1);
    }

    /**
     * Adds biomes to a new region for this world
     *
     * @param registry Registry that the biome is registered with
     * @param mapper Pairs of climate parameters mapped to biome resource keys
     */
    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
    {
        addModifiedVanillaOverworldBiomes(mapper, builder -> {
            if(ModConfiguration.spawnCoralForest.get()) {
                // TODO: This may look better replacing beach instead. It spawns smaller biomes next to the sea but with less likelihood of being near warm ocean corals cause of temperature differences
                //  I wonder if I could customize the parameter points on this to be a desert but coastal...?
                builder.replaceBiome(Biomes.BEACH, ModBiomeInitializer.CORAL_DESERT);
            }
        });
    }
}
