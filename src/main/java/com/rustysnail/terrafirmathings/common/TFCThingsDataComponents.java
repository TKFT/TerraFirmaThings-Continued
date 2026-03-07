package com.rustysnail.terrafirmathings.common;

import java.util.UUID;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TFCThingsDataComponents
{

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TerraFirmaThings.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> DISTANCE_TRAVELED =
        DATA_COMPONENTS.registerComponentType("distance_traveled",
            builder -> builder
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Boolean>> JAVELIN_THROWN =
        DATA_COMPONENTS.registerComponentType("javelin_thrown",
            builder -> builder
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL));

    public static final Supplier<DataComponentType<UUID>> JAVELIN_ENTITY_UUID =
        DATA_COMPONENTS.registerComponentType("javelin_entity_uuid",
            builder -> builder
                .persistent(UUIDUtil.CODEC)
                .networkSynchronized(UUIDUtil.STREAM_CODEC));

    public static final Supplier<DataComponentType<UUID>> CAPTURED_ENTITY_UUID =
        DATA_COMPONENTS.registerComponentType("captured_entity_uuid",
            builder -> builder
                .persistent(UUIDUtil.CODEC)
                .networkSynchronized(UUIDUtil.STREAM_CODEC));

    public static final Supplier<DataComponentType<Integer>> SHARPNESS_CHARGES =
        DATA_COMPONENTS.registerComponentType("sharpness_charges",
            builder -> builder
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.INT));
}
