package com.deku.darkdesolations.common.features;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;
import java.util.stream.Stream;

public class DeadClawCoralFeature extends DeadCoralFeature {
    public DeadClawCoralFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    /**
     * Attempts to place the given feature into the world.
     * Returns only when the feature has finished placing all its blocks.
     *
     * NOTE: Pretty much just a copy of how vanilla generates a claw coral feature in warm oceans, but with minor changes to only apply in water
     * NOTE AGAIN: This is incredibly similar to LandClawCoralFeature but since we are inheriting from a pretty static vanilla coral feature and I want to attach different generation capabilities to underwater vs land coral, I have essentially duplicated features
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

        // Attempt to place first coral block
        if (!this.placeCoralBlock(levelAccessor, random, position, blockState)) {
            return false;
        } else {
            // Generate positions for more coral blocks to be set
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int i = random.nextInt(2) + 2;
            List<Direction> list = Util.toShuffledList(Stream.of(direction, direction.getClockWise(), direction.getCounterClockWise()), random);

            // For each position, attempt to generate another coral block
            for(Direction direction1 : list.subList(0, i)) {
                BlockPos.MutableBlockPos mutableBlockPos = position.mutable();
                int j = random.nextInt(2) + 1;
                mutableBlockPos.move(direction1);
                int k;
                Direction direction2;
                if (direction1 == direction) {
                    direction2 = direction;
                    k = random.nextInt(3) + 2;
                } else {
                    mutableBlockPos.move(Direction.UP);
                    Direction[] adirection = new Direction[]{direction1, Direction.UP};
                    direction2 = Util.getRandom(adirection, random);
                    k = random.nextInt(3) + 3;
                }

                for(int l = 0; l < j && this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState); ++l) {
                    mutableBlockPos.move(direction2);
                }

                // Move upwards and attempt to place another coral block, breaking out of generation early if something causes the placement to fail
                mutableBlockPos.move(direction2.getOpposite());
                mutableBlockPos.move(Direction.UP);

                for(int i1 = 0; i1 < k; ++i1) {
                    mutableBlockPos.move(direction);
                    if (!this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState)) {
                        break;
                    }

                    if (random.nextFloat() < 0.25F) {
                        mutableBlockPos.move(Direction.UP);
                    }
                }
            }

            return true;
        }
    }
}
