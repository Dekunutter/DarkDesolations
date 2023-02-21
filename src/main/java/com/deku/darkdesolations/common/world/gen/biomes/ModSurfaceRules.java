package com.deku.darkdesolations.common.world.gen.biomes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModSurfaceRules {
    private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);

    /**
     * Makes a state rule based off of a given block
     *
     * @param block The block whose default state we want to turn into a surface rule
     * @return Rule for spreading a given block across the surface
     */
    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    /**
     * Builds the surface rules that this mod adds to the game.
     * Currently builds surface rules for:
     * - Cherry Blossom Slopes - to ensure dirt and snow layering similar to a vanilla grove biome
     *
     * @return The surface rules to be added to the game
     */
    public static SurfaceRules.RuleSource makeRules() {
        return SurfaceRules.sequence(
            SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomeInitializer.CORAL_DESERT),
                coralDesertSurfaceRules()
            )
        );
    }

    /**
     * Condition to check if the surface area is above water level
     *
     * @return Returns true if the surface area is above water level
     */
    private static SurfaceRules.ConditionSource isAboveWaterLevel() {
        return SurfaceRules.waterBlockCheck(0, 0);
    }

    /**
     * Condition to check if the surface area is at or above water level
     *
     * @return Returns true if the surface area is at or above water level
     */
    private static SurfaceRules.ConditionSource isAtOrAboveWaterLevel() {
        return SurfaceRules.waterBlockCheck(-1, 0);
    }

    /**
     * Condition to check if the surface area is slightly below water level
     * Slightly below water level being up to 6 blocks below.
     *
     * @return Returns true if the surface area is around 6 blocks below water level
     */
    private static SurfaceRules.ConditionSource isSlightlyBelowWaterLevel() {
        return SurfaceRules.waterStartCheck(-6, -1);
    }

    /**
     * Convert ceiling blocks to sandstone and layer the rest as sand
     *
     * @return The rule for layering sand with sandstone ceilings for any caves or overhangs
     */
    private static SurfaceRules.RuleSource sandWithSandstoneCeilings() {
        return SurfaceRules.sequence(
            SurfaceRules.ifTrue(
                SurfaceRules.ON_CEILING,
                SANDSTONE
            ),
            SAND
        );
    }

    /**
     * Builds the surface rules for the coral desert biome
     * These rules do the following:
     * - On the surface, layers of sand with sandstone for any ceilings
     * - Below the surface start to place more sandstone
     *
     * @return The surface rules for the coral desert biome
     */
    private static SurfaceRules.RuleSource coralDesertSurfaceRules() {
        // Surface rules for spawning sand, replacing some of the generated sand with sandstone for ceilings when above the water level
        SurfaceRules.RuleSource groundRules = SurfaceRules.ifTrue(
                SurfaceRules.ON_FLOOR,
                SurfaceRules.ifTrue(
                    isAtOrAboveWaterLevel(),
                    sandWithSandstoneCeilings()
                )
        );

        // Surface rules for spawning sand but with sandstone for ceiling blocks or at very deep zones
        SurfaceRules.RuleSource buriedGroundRules = SurfaceRules.ifTrue(
            isSlightlyBelowWaterLevel(),
            SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                    SurfaceRules.UNDER_FLOOR,
                    sandWithSandstoneCeilings()
                ),
                SurfaceRules.ifTrue(
                    SurfaceRules.VERY_DEEP_UNDER_FLOOR,
                    SANDSTONE
                )
            )
        );

        return SurfaceRules.ifTrue(
            SurfaceRules.abovePreliminarySurface(),
            SurfaceRules.sequence(
                groundRules,
                buriedGroundRules
            )
        );
    }
}


