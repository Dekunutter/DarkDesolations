package com.deku.darkdesolations.common.features;

import com.deku.darkdesolations.common.blocks.ModBlockTags;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.CoralFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Optional;

public abstract class LandCoralFeature extends CoralFeature {
    public LandCoralFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    /**
     * Attempts to place the feature into the world.
     * Gets a random coral block from the coral block tag group as the block state we want to pass to the feature placer
     *
     * @param placementContext The placement context for this feature, holding information about the world and origin position
     * @return Boolean value representing whether we successfully placed this feature into the world
     */
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placementContext) {
        RandomSource random = placementContext.random();
        WorldGenLevel level = placementContext.level();
        BlockPos position = placementContext.origin();
        Optional<Block> block = BuiltInRegistries.BLOCK.getTag(ModBlockTags.DEAD_CORAL_BLOCKS).flatMap((blocks) -> {
            return blocks.getRandomElement(random);
        }).map(Holder::value);
        return block.isEmpty() ? false : this.placeFeature(level, random, position, block.get().defaultBlockState());
    }

    @Override
    protected abstract boolean placeFeature(LevelAccessor levelAccessor, RandomSource random, BlockPos position, BlockState blockState);

    /**
     * Attempts to place a dead coral block into the level by replacing another block that's already been generated into the world.
     * Subsequently will then attempt to attach any decorations as further blocks, such as corals, or wall corals.
     *
     * NOTE: Pretty much just a copy of how vanilla generates coral features in warm oceans, but with minor changes to place dead corals on land and ensure they are only waterlogged when appropriate
     *
     * @param levelAccessor The accessor for information in the current level
     * @param random Random number generator
     * @param position Position we are attempting to place a coral block at
     * @param blockState The block state of the block we are trying to replace
     * @return
     */
    // TODO: Could inherit logic from DeadCoralFeature to reduce code...?
    @Override
    protected boolean placeCoralBlock(LevelAccessor levelAccessor, RandomSource random, BlockPos position, BlockState blockState) {
        BlockPos coralPosition = position.above();
        BlockState originBlockState = levelAccessor.getBlockState(position);
        BlockPos groundPosition = position.below();

        // TODO: Don't allow coral blocks to spawn on lava. Messes with lava springs in odd ways. Same for coral plants
        // Don't spawn coral block if current position is water or position below is water
        if (originBlockState.is(Blocks.WATER) || levelAccessor.getBlockState(groundPosition).is(Blocks.WATER)) {
            return false;
        }

        // Turn the current block into a coral block
        levelAccessor.setBlock(position, blockState, 3);

        // TODO: Don't spawn coral plants or wall plants if block was already occupied? Seeing weird cases where it overwrites a coral block or cuts through sugarcane. Looks unatural
        // Random chance to place a dead coral to decorate the previously placed coral block
        if (random.nextFloat() < 0.25F) {
            BuiltInRegistries.BLOCK.getTag(ModBlockTags.DEAD_CORALS).flatMap((blocks) -> {
                return blocks.getRandomElement(random);
            }).map(Holder::value).ifPresent((block) -> {
                BlockState coralBlockState = block.defaultBlockState();

                // mark as not waterlogged if the plant is not replacing water
                BlockState currentBlockState = levelAccessor.getBlockState(coralPosition);
                if (!currentBlockState.is(Blocks.WATER)) {
                    coralBlockState = coralBlockState.setValue(BlockStateProperties.WATERLOGGED, false);
                }

                levelAccessor.setBlock(coralPosition, coralBlockState, 2);
            });
        }

        // Random change to place a dead wall coral to decorate the previously placed coral block
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.2F) {
                BlockPos relativePosition = position.relative(direction);
                BuiltInRegistries.BLOCK.getTag(ModBlockTags.DEAD_WALL_CORALS).flatMap((blocks) -> {
                    return blocks.getRandomElement(random);
                }).map(Holder::value).ifPresent((block) -> {
                    BlockState relativeBlockState = block.defaultBlockState();
                    if (relativeBlockState.hasProperty(BaseCoralWallFanBlock.FACING)) {
                        relativeBlockState = relativeBlockState.setValue(BaseCoralWallFanBlock.FACING, direction);
                    }

                    // mark as not waterlogged if the wall plant is not replacing water
                    BlockState currentBlockState = levelAccessor.getBlockState(relativePosition);
                    if (!currentBlockState.is(Blocks.WATER)) {
                        relativeBlockState = relativeBlockState.setValue(BlockStateProperties.WATERLOGGED, false);
                    }

                    levelAccessor.setBlock(relativePosition, relativeBlockState, 2);
                });
            }
        }

        return true;
    }
}
