package com.deku.darkdesolations.common.items;

import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ObjectHolder;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModItems {
    @ObjectHolder(registryName = "minecraft:item", value = MOD_ID + ":coralfish_spawn_egg")
    public static ForgeSpawnEggItem CORALFISH_SPAWN_EGG;
}
