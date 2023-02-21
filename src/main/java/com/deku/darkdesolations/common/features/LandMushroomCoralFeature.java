package com.deku.darkdesolations.common.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionDefaults;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LandMushroomCoralFeature extends LandCoralFeature {
    public LandMushroomCoralFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    /**
     * Attempts to place the given feature into the world.
     * Returns only when the feature has finished placing all its blocks.
     *
     * NOTE: Pretty much just a copy of how vanilla generates a mushroom coral feature in warm oceans, but with minor changes to only apply in water
     * NOTE AGAIN: This is incredibly similar to DeadMushroomCoralFeature but since we are inheriting from a pretty static vanilla coral feature and I want to attach different generation capabilities to underwater vs land coral, I have essentially duplicated features
     *
     * @param levelAccessor Accessor for the level information
     * @param random Random number generator
     * @param position Position the feature is generating from
     * @param blockState State of the block we are placing to start this feature
     * @return Whether the feature was successfully placed or not
     */
    @Override
    protected boolean placeFeature(LevelAccessor levelAccessor, RandomSource random, BlockPos position, BlockState blockState) {
        // Generate random integers to represent the bounds of this mushroom coral
        int i = random.nextInt(3) + 3;
        int j = random.nextInt(3) + 3;
        int k = random.nextInt(3) + 3;
        int l = random.nextInt(3) + 1;
        BlockPos.MutableBlockPos mutableBlockPos = position.mutable();

        // For each position in our bounds, attempt to place a coral block and move down to the next layer to place further if conditions are right
        int num = 0;
        for(int i1 = 0; i1 <= j; ++i1) {
            for(int j1 = 0; j1 <= i; ++j1) {
                for(int k1 = 0; k1 <= k; ++k1) {
                    mutableBlockPos.set(i1 + position.getX(), j1 + position.getY(), k1 + position.getZ());
                    mutableBlockPos.move(Direction.DOWN, l);

                    // New logic I added to exit early and don't generate the mushroom if the starting point is above water... otherwise they tended to float over the water unnaturally
                    if (num == 0) {
                        BlockPos belowPosition = position.below();
                        int steps = 0;
                        while (belowPosition.getY() >= DimensionDefaults.OVERWORLD_MIN_Y && steps < 10) {
                            steps++;
                            BlockState belowState = levelAccessor.getBlockState(belowPosition);
                            if (!levelAccessor.isEmptyBlock(belowPosition)) {
                                if (belowState.is(Blocks.WATER)) {
                                    return false;
                                }
                                break;
                            }
                        }
                    }

                    if ((i1 != 0 && i1 != j || j1 != 0 && j1 != i) && (k1 != 0 && k1 != k || j1 != 0 && j1 != i) && (i1 != 0 && i1 != j || k1 != 0 && k1 != k) && (i1 == 0 || i1 == j || j1 == 0 || j1 == i || k1 == 0 || k1 == k) && !(random.nextFloat() < 0.1F) && !this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState)) {
                    }
                    num++;
                }
            }
        }

        return true;
    }
}
