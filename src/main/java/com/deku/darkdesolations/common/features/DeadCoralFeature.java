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
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.CoralFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Optional;

public abstract class DeadCoralFeature extends CoralFeature {
    public DeadCoralFeature(Codec<NoneFeatureConfiguration> codec) {
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
        RandomSource randomsource = placementContext.random();
        WorldGenLevel worldgenlevel = placementContext.level();
        BlockPos blockpos = placementContext.origin();
        Optional<Block> optional = BuiltInRegistries.BLOCK.getTag(ModBlockTags.DEAD_CORAL_BLOCKS).flatMap((blocks) -> {
            return blocks.getRandomElement(randomsource);
        }).map(Holder::value);
        return optional.isEmpty() ? false : this.placeFeature(worldgenlevel, randomsource, blockpos, optional.get().defaultBlockState());
    }

    @Override
    protected abstract boolean placeFeature(LevelAccessor levelAccessor, RandomSource randomSource, BlockPos blockPos, BlockState blockState);

    /**
     * Attempts to place a dead coral block into the level by replacing another block that's already been generated into the world.
     * Subsequently will then attempt to attach any decorations as further blocks, such as corals, wall corals, or sea pickles.
     *
     * NOTE: Pretty much just a copy of how vanilla generates coral features in warm oceans, but with minor changes to place dead corals instead and ensure they are only waterlogged when appropriate
     *
     * @param levelAccessor The accessor for information in the current level
     * @param random Random number generator
     * @param position Position we are attempting to place a coral block at
     * @param blockState The block state of the block we are trying to replace
     * @return
     */
    @Override
    protected boolean placeCoralBlock(LevelAccessor levelAccessor, RandomSource random, BlockPos position, BlockState blockState) {
        BlockPos coralPosition = position.above();
        levelAccessor.setBlock(position, blockState, 3);

        // TODO: Don't spawn coral plants or wall plants if block was already occupied? Seeing weird cases where it overwrites a coral block or cuts through sugarcane. Looks unnatural. See what the FEATURES_CANNOT_REPLACE tag could do for us since thats used in hotspring generation?
        // Random chance to place a dead coral to decorate the previously placed coral block
        if (random.nextFloat() < 0.25F) {
            BuiltInRegistries.BLOCK.getTag(ModBlockTags.DEAD_CORALS).flatMap((blocks) -> {
                return blocks.getRandomElement(random);
            }).map(Holder::value).ifPresent((block) -> {
                BlockState coralBlockState = block.defaultBlockState();

                coralBlockState = setWaterloggedState(levelAccessor, coralPosition, coralBlockState);

                levelAccessor.setBlock(coralPosition, coralBlockState, 2);
            });
        } else if (random.nextFloat() < 0.05F) {
            // TODO: Should sea pickles potentially spawn above land at all?? Maybe do a water check on this or remove entirely
            BlockState pickleBlockState = Blocks.SEA_PICKLE.defaultBlockState();

            pickleBlockState = setWaterloggedState(levelAccessor, coralPosition, pickleBlockState);

            levelAccessor.setBlock(coralPosition, pickleBlockState.setValue(SeaPickleBlock.PICKLES, Integer.valueOf(random.nextInt(4) + 1)), 2);
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

                    relativeBlockState = setWaterloggedState(levelAccessor, coralPosition, relativeBlockState);

                    levelAccessor.setBlock(relativePosition, relativeBlockState, 2);
                });
            }
        }

        return true;
    }

    /**
     * Checks if the block at the given position is water
     *
     * @param levelAccessor The accessor for information of the level
     * @param position Position of the block we are checking
     * @return Whether the block at the given position in the level is water
     */
    protected boolean isBlockWater(LevelAccessor levelAccessor, BlockPos position) {
        BlockState originBlockState = levelAccessor.getBlockState(position);
        if (!originBlockState.is(Blocks.WATER)) {
            return false;
        }
        return true;
    }

    /**
     * Sets the waterlogged state appropriately onto the coral depending on if the block that it is replacing is water or not
     * NOTE: Corals have waterlogged defaulted to true, so by just returning the block state unchanged we assume the coral will be waterlogged
     *
     * @param levelAccessor Accessor for information about the level
     * @param position Position of the block being replaced
     * @param blockState State of the coral being placed
     * @return Updated block state with the correct waterlogged state set
     */
    private BlockState setWaterloggedState(LevelAccessor levelAccessor, BlockPos position, BlockState blockState) {
        BlockState currentBlockState = levelAccessor.getBlockState(position);
        if (!currentBlockState.is(Blocks.WATER)) {
            blockState = blockState.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        return blockState;
    }
}
