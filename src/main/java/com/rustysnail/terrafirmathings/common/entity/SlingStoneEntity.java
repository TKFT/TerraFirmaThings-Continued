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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class SlingStoneEntity extends AbstractArrow
{
    private static final int GROUND_LIFESPAN = 6000;

    private static final EntityDataAccessor<Integer> DATA_AMMO_TYPE =
        SynchedEntityData.defineId(SlingStoneEntity.class, EntityDataSerializers.INT);

    private static final TagKey<EntityType<?>> LAND_PREDATORS =
        TagKey.create(Registries.ENTITY_TYPE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tfc", "land_predators"));
    private static final TagKey<EntityType<?>> OCEAN_PREDATORS =
        TagKey.create(Registries.ENTITY_TYPE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tfc", "ocean_predators"));
    private static final TagKey<EntityType<?>> SKELETONS =
        TagKey.create(Registries.ENTITY_TYPE, net.minecraft.resources.ResourceLocation.parse("minecraft:skeletons"));

    public SlingStoneEntity(EntityType<? extends SlingStoneEntity> type, Level level)
    {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public SlingStoneEntity(Level level, LivingEntity thrower, float power,
                            SlingAmmoItem.AmmoType ammoType, ItemStack ammoItem)
    {
        super(TFCThingsEntities.SLING_STONE.get(), thrower, level,
            ammoItem.isEmpty() ? defaultItemFor(ammoType) : ammoItem.copyWithCount(1),
            null);
        setBaseDamage(power);
        setAmmoType(ammoType);
        this.pickup = ammoType.isRecoverable() ? Pickup.ALLOWED : Pickup.DISALLOWED;
        if (ammoType.setsFire())
        {
            this.setRemainingFireTicks(200);
        }
    }

    private static ItemStack defaultItemFor(SlingAmmoItem.AmmoType ammoType)
    {
        return switch (ammoType)
        {
            case LIGHT -> new ItemStack(TFCThingsItems.SLING_AMMO_LIGHT.get());
            case SCATTER -> new ItemStack(TFCThingsItems.SLING_AMMO_SPREAD.get());
            case FIRE -> new ItemStack(TFCThingsItems.SLING_AMMO_FIRE.get());
            default -> new ItemStack(TFCThingsItems.SLING_AMMO_HEAVY.get());
        };
    }

    public SlingAmmoItem.AmmoType getAmmoType()
    {
        int ord = this.entityData.get(DATA_AMMO_TYPE);
        SlingAmmoItem.AmmoType[] values = SlingAmmoItem.AmmoType.values();
        return (ord >= 0 && ord < values.length) ? values[ord] : SlingAmmoItem.AmmoType.HEAVY;
    }

    public void setAmmoType(SlingAmmoItem.AmmoType type)
    {
        this.entityData.set(DATA_AMMO_TYPE, type.ordinal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_AMMO_TYPE, 0);
    }

    @Override
    protected double getDefaultGravity()
    {
        return getAmmoType().getGravity();
    }

    @Override
    public void tickDespawn()
    {
        if (this.pickup == Pickup.ALLOWED)
        {
            if (this.inGroundTime >= GROUND_LIFESPAN)
            {
                this.discard();
            }
        }
        else
        {
            super.tickDespawn();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        if (level().isClientSide()) return;

        float damage = (float) getBaseDamage();
        if (result.getEntity() instanceof LivingEntity target)
        {
            double multiplier = TFCThingsConfig.ITEMS.SLING.predatorMultiplier.get();
            if (target.getType().is(LAND_PREDATORS)
                || target.getType().is(OCEAN_PREDATORS)
                || target.getType().is(SKELETONS))
            {
                damage *= (float) multiplier;
            }
            if (getAmmoType().setsFire())
            {
                target.igniteForSeconds(5);
            }
        }

        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), damage);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result)
    {
        if (getAmmoType().isRecoverable())
        {
            super.onHitBlock(result);
        }
        else
        {
            if (!level().isClientSide())
            {
                discard();
            }
        }
    }

    @Override
    public ItemStack getPickupItem()
    {
        ItemStack origin = this.getPickupItemStackOrigin();
        return origin.isEmpty() ? defaultItemFor(getAmmoType()) : origin.copy();
    }

    @Override
    protected ItemStack getDefaultPickupItem()
    {
        return defaultItemFor(getAmmoType());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt("AmmoType", getAmmoType().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        int ord = tag.getInt("AmmoType");
        SlingAmmoItem.AmmoType[] values = SlingAmmoItem.AmmoType.values();
        setAmmoType((ord >= 0 && ord < values.length) ? values[ord] : SlingAmmoItem.AmmoType.HEAVY);
    }
}
