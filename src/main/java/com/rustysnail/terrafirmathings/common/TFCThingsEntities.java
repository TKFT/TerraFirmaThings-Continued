package com.rustysnail.terrafirmathings.common;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.entity.SlingStoneEntity;
import com.rustysnail.terrafirmathings.common.entity.ThrownHookJavelin;
import com.rustysnail.terrafirmathings.common.entity.ThrownRopeBridge;
import com.rustysnail.terrafirmathings.common.entity.ThrownRopeJavelin;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TFCThingsEntities
{

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, TerraFirmaThings.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownRopeBridge>> THROWN_ROPE_BRIDGE =
        ENTITY_TYPES.register("thrown_rope_bridge", () -> EntityType.Builder.<ThrownRopeBridge>of(ThrownRopeBridge::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(128)
            .updateInterval(1)
            .build("thrown_rope_bridge"));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownRopeJavelin>> THROWN_ROPE_JAVELIN =
        ENTITY_TYPES.register("thrown_rope_javelin", () -> EntityType.Builder.<ThrownRopeJavelin>of(ThrownRopeJavelin::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(128)
            .updateInterval(1)
            .build("thrown_rope_javelin"));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownHookJavelin>> THROWN_HOOK_JAVELIN =
        ENTITY_TYPES.register("thrown_hook_javelin", () -> EntityType.Builder.<ThrownHookJavelin>of(ThrownHookJavelin::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(128)
            .updateInterval(1)
            .build("thrown_hook_javelin"));

    public static final DeferredHolder<EntityType<?>, EntityType<SlingStoneEntity>> SLING_STONE =
        ENTITY_TYPES.register("sling_stone", () -> EntityType.Builder.<SlingStoneEntity>of(SlingStoneEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(64)
            .updateInterval(10)
            .build("sling_stone"));
}
