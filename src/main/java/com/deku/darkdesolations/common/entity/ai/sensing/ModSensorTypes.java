package com.deku.darkdesolations.common.entity.ai.sensing;

import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.deku.darkdesolations.Main.MOD_ID;

public class ModSensorTypes<U extends Sensor<?>> {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, MOD_ID);
}
