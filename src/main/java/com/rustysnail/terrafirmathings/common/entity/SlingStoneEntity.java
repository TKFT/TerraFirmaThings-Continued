package com.rustysnail.terrafirmathings.common.entity;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.item.SlingAmmoItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class SlingStoneEntity extends ThrowableProjectile implements ItemSupplier
{

    private static final EntityDataAccessor<Float> DATA_POWER =
        SynchedEntityData.defineId(SlingStoneEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_AMMO_TYPE =
        SynchedEntityData.defineId(SlingStoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ON_FIRE =
        SynchedEntityData.defineId(SlingStoneEntity.class, EntityDataSerializers.BOOLEAN);

    private static final TagKey<EntityType<?>> LAND_PREDATORS =
        TagKey.create(Registries.ENTITY_TYPE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tfc", "land_predators"));
    private static final TagKey<EntityType<?>> OCEAN_PREDATORS =
        TagKey.create(Registries.ENTITY_TYPE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tfc", "ocean_predators"));
    private static final TagKey<EntityType<?>> SKELETONS =
        TagKey.create(Registries.ENTITY_TYPE, net.minecraft.resources.ResourceLocation.parse("minecraft:skeletons"));

    public SlingStoneEntity(EntityType<? extends SlingStoneEntity> type, Level level)
    {
        super(type, level);
    }

    public SlingStoneEntity(Level level, LivingEntity thrower, float power, SlingAmmoItem.AmmoType ammoType)
    {
        super(TFCThingsEntities.SLING_STONE.get(), thrower, level);
        setPower(power);
        setAmmoType(ammoType);
        if (ammoType.setsFire())
        {
            setOnFireFlag(true);
            this.setRemainingFireTicks(200);
        }
    }

    @Override
    public ItemStack getItem()
    {
        return new ItemStack(TFCThingsItems.SLING_AMMO.get());
    }

    public float getPower()
    {
        return this.entityData.get(DATA_POWER);
    }

    public void setPower(float power)
    {
        this.entityData.set(DATA_POWER, power);
    }

    public SlingAmmoItem.AmmoType getAmmoType()
    {
        int ord = this.entityData.get(DATA_AMMO_TYPE);
        SlingAmmoItem.AmmoType[] values = SlingAmmoItem.AmmoType.values();
        return ord >= 0 && ord < values.length ? values[ord] : SlingAmmoItem.AmmoType.HEAVY;
    }

    public void setAmmoType(SlingAmmoItem.AmmoType type)
    {
        this.entityData.set(DATA_AMMO_TYPE, type.ordinal());
    }

    public boolean getOnFireFlag()
    {
        return this.entityData.get(DATA_ON_FIRE);
    }

    public void setOnFireFlag(boolean onFire)
    {
        this.entityData.set(DATA_ON_FIRE, onFire);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        builder.define(DATA_POWER, 0f);
        builder.define(DATA_AMMO_TYPE, 0);
        builder.define(DATA_ON_FIRE, false);
    }

    @Override
    protected double getDefaultGravity()
    {
        return getAmmoType().getGravity();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Power", getPower());
        tag.putInt("AmmoType", getAmmoType().ordinal());
        tag.putBoolean("OnFire", getOnFireFlag());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setPower(tag.getFloat("Power"));
        int ord = tag.getInt("AmmoType");
        SlingAmmoItem.AmmoType[] values = SlingAmmoItem.AmmoType.values();
        setAmmoType(ord >= 0 && ord < values.length ? values[ord] : SlingAmmoItem.AmmoType.HEAVY);
        setOnFireFlag(tag.getBoolean("OnFire"));
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        if (level().isClientSide()) return;

        float damage = getPower();
        if (result.getEntity() instanceof LivingEntity target)
        {
            double multiplier = TFCThingsConfig.ITEMS.SLING.predatorMultiplier.get();
            if (target.getType().is(LAND_PREDATORS) || target.getType().is(OCEAN_PREDATORS) || target.getType().is(SKELETONS))
            {
                damage *= (float) multiplier;
            }

            if (getOnFireFlag())
            {
                target.igniteForSeconds(5);
            }
        }

        DamageSource source = this.damageSources().thrown(this, this.getOwner());
        result.getEntity().hurt(source, damage);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result)
    {
        if (!level().isClientSide())
        {
            this.discard();
        }
    }
}
