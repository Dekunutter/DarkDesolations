package com.deku.darkdesolations.common.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModBlockTags {
    public static final TagKey<Block> DEAD_CORALS = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "dead_corals"));
    public static final TagKey<Block> DEAD_CORAL_PLANTS = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "dead_coral_plants"));
    public static final TagKey<Block> DEAD_CORAL_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "dead_coral_blocks"));

    public static final TagKey<Block> DEAD_WALL_CORALS = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "dead_wall_corals"));
}
