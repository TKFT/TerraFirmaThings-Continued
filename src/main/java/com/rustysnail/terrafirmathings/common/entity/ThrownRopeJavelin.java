package com.rustysnail.terrafirmathings.common.entity;

import java.util.UUID;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.item.RopeJavelinItem;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownRopeJavelin extends AbstractArrow implements ItemSupplier
{

    private static final EntityDataAccessor<ItemStack> DATA_WEAPON =
        SynchedEntityData.defineId(ThrownRopeJavelin.class, EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<Boolean> DATA_RETRACTING =
        SynchedEntityData.defineId(ThrownRopeJavelin.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    private UUID capturedEntityUuid;

    public ThrownRopeJavelin(EntityType<? extends ThrownRopeJavelin> type, Level level)
    {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public ThrownRopeJavelin(Level level, LivingEntity thrower, ItemStack weapon)
    {
        super(TFCThingsEntities.THROWN_ROPE_JAVELIN.get(), thrower, level, weapon, null);
        this.setWeapon(weapon.copy());
        this.pickup = Pickup.DISALLOWED;
    }

    public ItemStack getWeapon()
    {
        return this.entityData.get(DATA_WEAPON);
    }

    public void setWeapon(ItemStack stack)
    {
        this.entityData.set(DATA_WEAPON, stack.copy());
    }

    @Override
    public ItemStack getItem()
    {
        ItemStack weapon = getWeapon();
        return weapon.isEmpty() ? new ItemStack(TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.get()) : weapon;
    }

    public void startAutoRetract()
    {
        clearCapturedEntity(this.getOwner());
        startRetracting();
        setNoGravity(true);
        setNoPhysics(true);
        this.inGround = false;
    }

    public void pullCapturedTowardOwner(float strength)
    {
        if (this.level().isClientSide())
        {
            return;
        }

        Entity owner = this.getOwner();
        Entity captured = getCapturedEntity();
        if (owner == null || captured == null)
        {
            return;
        }

        Vec3 ownerCenter = owner.position().add(0, owner.getBbHeight() * 0.75, 0);
        Vec3 targetCenter = captured.position().add(0, captured.getBbHeight() * 0.5, 0);
        Vec3 toOwner = ownerCenter.subtract(targetCenter);

        if (toOwner.lengthSqr() <= 1.0e-6)
        {
            return;
        }

        if (captured.isPassenger())
        {
            captured.stopRiding();
        }

        captured.setDeltaMovement(captured.getDeltaMovement().add(toOwner.normalize().scale(strength)));
        captured.hurtMarked = true;
    }

    public boolean canPullCapturedTowardOwner()
    {
        if (isRetracting())
        {
            return false;
        }

        Entity owner = this.getOwner();
        Entity captured = getCapturedEntity();
        if (owner == null || captured == null || !owner.isAlive() || !captured.isAlive())
        {
            return false;
        }

        Vec3 ownerAnchor = getOwnerAnchor(owner);
        int ropeLength = TFCThingsConfig.ITEMS.ROPE_JAVELIN.ropeLength.get();
        return this.position().distanceTo(ownerAnchor) <= ropeLength
            && !isRopePathBlocked(ownerAnchor, this.position());
    }

    @Nullable
    public Entity getCapturedEntity()
    {
        if (capturedEntityUuid == null)
        {
            return null;
        }
        if (this.level() instanceof ServerLevel serverLevel)
        {
            return serverLevel.getEntity(capturedEntityUuid);
        }
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_WEAPON, ItemStack.EMPTY);
        builder.define(DATA_RETRACTING, false);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.level().isClientSide())
        {
            return;
        }

        Entity owner = this.getOwner();

        if (this.tickCount <= 3)
        {
            if (owner == null || !owner.isAlive())
            {
                this.discard();
            }
            return;
        }

        if (isRetracting())
        {
            tickRetract(owner);
            return;
        }

        if (shouldRetract(owner))
        {
            clearOwnerThrownState(owner);
            this.discard();
            return;
        }

        Entity captured = getCapturedEntity();
        if (captured != null)
        {
            if (!captured.isAlive())
            {
                clearCapturedEntity(owner);
                startAutoRetract();
                return;
            }

            this.setNoGravity(true);
            this.inGround = false;
            this.setDeltaMovement(Vec3.ZERO);
            this.setPos(captured.getX(), captured.getBoundingBox().minY + captured.getBbHeight() * 0.8D, captured.getZ());

            Vec3 ownerAnchor = getOwnerAnchor(owner);
            int ropeLength = TFCThingsConfig.ITEMS.ROPE_JAVELIN.ropeLength.get();
            if (this.position().distanceTo(ownerAnchor) > ropeLength || isRopePathBlocked(ownerAnchor, this.position()))
            {
                startAutoRetract();
            }
            return;
        }

        this.setNoGravity(false);

        Vec3 ownerAnchor = getOwnerAnchor(owner);
        int ropeLength = TFCThingsConfig.ITEMS.ROPE_JAVELIN.ropeLength.get();

        if (!this.inGround)
        {
            if (this.position().distanceTo(ownerAnchor) > ropeLength)
            {
                startAutoRetract();
            }
            return;
        }

        if (this.position().distanceTo(ownerAnchor) > ropeLength || isRopePathBlocked(ownerAnchor, this.position()))
        {
            startAutoRetract();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();

        if (target == owner)
        {
            return;
        }

        DamageSource source = this.damageSources().arrow(this, owner != null ? owner : this);
        boolean hurt = target.hurt(source, 4.0F);

        if (hurt)
        {
            if (target.isPassenger())
            {
                target.stopRiding();
            }

            capturedEntityUuid = target.getUUID();
            this.inGround = false;
            this.setNoGravity(true);
            this.setDeltaMovement(Vec3.ZERO);

            syncCapturedToOwnerItem(owner, capturedEntityUuid);
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        }
        else
        {
            startAutoRetract();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result)
    {
        super.onHitBlock(result);
        if (!this.level().isClientSide())
        {
            this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        ItemStack weapon = getWeapon();
        if (!weapon.isEmpty())
        {
            tag.put("Weapon", weapon.save(this.registryAccess()));
        }
        if (capturedEntityUuid != null)
        {
            tag.putUUID("CapturedEntity", capturedEntityUuid);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Weapon"))
        {
            setWeapon(ItemStack.parse(this.registryAccess(), tag.getCompound("Weapon")).orElse(ItemStack.EMPTY));
        }
        capturedEntityUuid = tag.hasUUID("CapturedEntity") ? tag.getUUID("CapturedEntity") : null;
    }

    @Override
    protected ItemStack getDefaultPickupItem()
    {
        return getWeapon();
    }

    private boolean shouldRetract(@Nullable Entity owner)
    {
        if (owner == null || !owner.isAlive()) return true;
        if (!(owner instanceof LivingEntity living)) return true;
        return findLinkedThrownStack(living).isEmpty();
    }

    private boolean isRetracting()
    {
        return this.entityData.get(DATA_RETRACTING);
    }

    private void startRetracting()
    {
        this.entityData.set(DATA_RETRACTING, true);
    }

    private void tickRetract(@Nullable Entity owner)
    {
        if (owner == null)
        {
            this.discard();
            return;
        }

        this.inGround = false;
        Vec3 anchor = getOwnerAnchor(owner);
        Vec3 toAnchor = anchor.subtract(this.position());
        double dist = toAnchor.length();

        if (dist < 1.0)
        {
            clearOwnerThrownState(owner);
            this.discard();
            return;
        }

        Vec3 step = toAnchor.normalize().scale(Math.min(1.25D, dist));
        Vec3 next = this.position().add(step);
        this.setPos(next.x, next.y, next.z);
        this.setDeltaMovement(Vec3.ZERO);
    }

    private Vec3 getOwnerAnchor(@Nullable Entity owner)
    {
        if (owner == null)
        {
            return this.position();
        }
        return owner.position().add(0, owner.getBbHeight() * 0.75, 0);
    }

    private boolean isRopePathBlocked(Vec3 from, Vec3 to)
    {
        BlockHitResult hit = this.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() != HitResult.Type.BLOCK)
        {
            return false;
        }

        BlockPos javelinBlockPos = this.blockPosition();
        if (hit.getBlockPos().equals(javelinBlockPos))
        {
            return false;
        }

        double totalDistSq = from.distanceToSqr(to);
        double hitDistSq = from.distanceToSqr(hit.getLocation());
        return hitDistSq + 0.01D < totalDistSq;
    }

    private void syncCapturedToOwnerItem(@Nullable Entity owner, @Nullable UUID capturedUuid)
    {
        if (!(owner instanceof LivingEntity living))
        {
            return;
        }

        ItemStack linked = findLinkedThrownStack(living);
        if (!linked.isEmpty())
        {
            RopeJavelinItem.setCapturedEntity(linked, capturedUuid);
        }
    }

    private void clearCapturedEntity(@Nullable Entity owner)
    {
        capturedEntityUuid = null;
        syncCapturedToOwnerItem(owner, null);
    }

    private void clearOwnerThrownState(@Nullable Entity owner)
    {
        if (!(owner instanceof LivingEntity living))
        {
            return;
        }

        ItemStack linked = findLinkedThrownStack(living);
        if (!linked.isEmpty())
        {
            RopeJavelinItem.clearThrownState(linked);
        }
    }

    private ItemStack findLinkedThrownStack(LivingEntity living)
    {
        ItemStack mainHand = living.getMainHandItem();
        if (RopeJavelinItem.isLinkedThrownStack(mainHand, this.getUUID()))
        {
            return mainHand;
        }

        ItemStack offHand = living.getOffhandItem();
        if (RopeJavelinItem.isLinkedThrownStack(offHand, this.getUUID()))
        {
            return offHand;
        }

        return ItemStack.EMPTY;
    }
}
