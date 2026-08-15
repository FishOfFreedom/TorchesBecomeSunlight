package com.freefish.torchesbecomesunlight.server.init.village;

import net.minecraft.core.registries.BuiltInRegistries;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.sensor.FindArmorStandSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class SensorTypeHandle {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE,TorchesBecomeSunlight.MOD_ID);

    public static final DeferredHolder<SensorType<?>, SensorType<FindArmorStandSensor>> FIND_ARMOR_STAND_SENSOR = registry("find_armor_stand", FindArmorStandSensor::new);

    private static <U extends Sensor<?>> DeferredHolder<SensorType<?>, SensorType<U>> registry(String name, Supplier<U> pSensorSupplier){
        return SENSOR_TYPES.register(name ,() -> new SensorType<>(pSensorSupplier));
    }
}
