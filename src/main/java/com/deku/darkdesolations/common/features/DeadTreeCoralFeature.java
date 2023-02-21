package com.deku.darkdesolations.common.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class DeadTreeCoralFeature extends DeadCoralFeature {
    public DeadTreeCoralFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    /**
     * Attempts to place the given feature into the world.
     * Returns only when the feature has finished placing all its blocks.
     *
     * NOTE: Pretty much just a copy of how vanilla generates a tree coral feature in warm oceans, but with minor changes to only apply in water
     * NOTE AGAIN: This is incredibly similar to LandTreeCoralFeature but since we are inheriting from a pretty static vanilla coral feature and I want to attach different generation capabilities to underwater vs land coral, I have essentially duplicated features
     *
     * @param levelAccessor Accessor for the level information
     * @param random Random number generator
     * @param position Position the feature is generating from
     * @param blockState State of the block we are placing to start this feature
     * @return Whether the feature was successfully placed or not
     */
    @Override
    protected boolean placeFeature(LevelAccessor levelAccessor, RandomSource random, BlockPos position, BlockState blockState) {
        // Don't bother placing this if we're not starting in water
        if (!isBlockWater(levelAccessor, position)) {
            return false;
        }

        // Generate how high we want our coral tree to grow and for each position vertically, place a coral block
        BlockPos.MutableBlockPos mutableBlockPos = position.mutable();
        int i = random.nextInt(3) + 1;

        for(int j = 0; j < i; ++j) {
            if (!this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState)) {
                return true;
            }

            mutableBlockPos.move(Direction.UP);
        }

        // Determine length of branch for our coral tree and for each position attempt to place a coral block and move upwards so that branches are always reaching up
        BlockPos blockpos = mutableBlockPos.immutable();
        int k = random.nextInt(3) + 2;
        List<Direction> list = Direction.Plane.HORIZONTAL.shuffledCopy(random);

        for(Direction direction : list.subList(0, k)) {
            mutableBlockPos.set(blockpos);
            mutableBlockPos.move(direction);
            int l = random.nextInt(5) + 2;
            int i1 = 0;

            for(int j1 = 0; j1 < l && this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState); ++j1) {
                ++i1;
                mutableBlockPos.move(Direction.UP);
                if (j1 == 0 || i1 >= 2 && random.nextFloat() < 0.25F) {
                    mutableBlockPos.move(direction);
                    i1 = 0;
                }
            }
        }

        return true;
    }
}
