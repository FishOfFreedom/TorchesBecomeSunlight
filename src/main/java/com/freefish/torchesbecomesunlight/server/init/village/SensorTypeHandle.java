package com.freefish.torchesbecomesunlight.server.init.village;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.entity.villager.villager.sensor.FindArmorStandSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SensorTypeHandle {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES,TorchesBecomeSunlight.MOD_ID);

    public static final RegistryObject<SensorType<FindArmorStandSensor>> FIND_ARMOR_STAND_SENSOR = registry("find_armor_stand", FindArmorStandSensor::new);

    private static <U extends Sensor<?>> RegistryObject<SensorType<U>> registry(String name, Supplier<U> pSensorSupplier){
        return SENSOR_TYPES.register(name ,() -> new SensorType<>(pSensorSupplier));
    }
}
