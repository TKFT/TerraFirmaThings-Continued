package com.rustysnail.terrafirmathings.common.condition;

import com.mojang.serialization.MapCodec;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class TFCThingsRecipeConditions
{
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
        DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, TerraFirmaThings.MOD_ID);

    @SuppressWarnings("unused")
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<? extends ICondition>> CONFIG_ENABLED =
        CONDITION_CODECS.register("config_enabled", () -> TFCThingsConfigCondition.CODEC);
}
