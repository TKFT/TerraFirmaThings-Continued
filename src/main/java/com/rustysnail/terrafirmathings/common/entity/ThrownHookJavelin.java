package com.rustysnail.terrafirmathings.common.entity;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.item.HookJavelinItem;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownHookJavelin extends AbstractArrow implements ItemSupplier
{
    private static final EntityDataAccessor<ItemStack> DATA_WEAPON =
        SynchedEntityData.defineId(ThrownHookJavelin.class, EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<Float> DATA_ROPE_LENGTH =
        SynchedEntityData.defineId(ThrownHookJavelin.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> DATA_IN_GROUND_SYNCED =
        SynchedEntityData.defineId(ThrownHookJavelin.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DATA_RETRACTING =
        SynchedEntityData.defineId(ThrownHookJavelin.class, EntityDataSerializers.BOOLEAN);

    private static double clamp(double value, double min, double max)
    {
        return Math.max(min, Math.min(max, value));
    }

    public ThrownHookJavelin(EntityType<? extends ThrownHookJavelin> type, Level level)
    {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public ThrownHookJavelin(Level level, LivingEntity thrower, ItemStack weapon)
    {
        super(TFCThingsEntities.THROWN_HOOK_JAVELIN.get(), thrower, level, weapon, null);
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
        return weapon.isEmpty() ? new ItemStack(TFCThingsItems.STEEL_HOOK_JAVELIN.get()) : weapon;
    }

    public float getRopeLength()
    {
        return this.entityData.get(DATA_ROPE_LENGTH);
    }

    public void setRopeLength(float length)
    {
        float maxLength = TFCThingsConfig.ITEMS.HOOK_JAVELIN.maxRopeLength.get().floatValue();
        this.entityData.set(DATA_ROPE_LENGTH, Math.max(1.0F, Math.min(length, maxLength)));
    }

    public boolean isInGroundSynced()
    {
        return this.entityData.get(DATA_IN_GROUND_SYNCED);
    }

    public void retractRope(float amount)
    {
        setRopeLength(getRopeLength() - amount);
    }

    public void extendRope(float amount)
    {
        setRopeLength(getRopeLength() + amount);
    }

    public void shortenAndPullOwner(float amount)
    {
        if (this.level().isClientSide())
        {
            return;
        }

        retractRope(amount);
    }

    public boolean isRetracting()
    {
        return this.entityData.get(DATA_RETRACTING);
    }

    private void setRetracting(boolean value)
    {
        this.entityData.set(DATA_RETRACTING, value);
    }

    public void startAutoRetract()
    {
        setRetracting(true);
        this.setNoGravity(true);
        this.setNoPhysics(true);
        this.inGround = false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_WEAPON, ItemStack.EMPTY);
        builder.define(DATA_ROPE_LENGTH, 60.0F);
        builder.define(DATA_IN_GROUND_SYNCED, false);
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

        if (shouldDiscard(owner))
        {
            clearOwnerThrownState(owner);
            this.discard();
            return;
        }

        this.entityData.set(DATA_IN_GROUND_SYNCED, this.inGround);

        float maxLength = TFCThingsConfig.ITEMS.HOOK_JAVELIN.maxRopeLength.get().floatValue();
        Vec3 ownerAnchor = getOwnerAnchor(owner);
        double distanceToOwner = this.position().distanceTo(ownerAnchor);
        if (distanceToOwner > maxLength)
        {
            startAutoRetract();
            return;
        }

        if ((this.inGround || isInGroundSynced()) && owner instanceof Player player)
        {
            if (player.isShiftKeyDown())
            {
                extendRope(0.2F);
            }

            float ropeLength = getRopeLength();

            if (!player.onGround() && this.getY() > player.getY() && distanceToOwner > ropeLength)
            {
                player.fallDistance = 0;

                Vec3 hookPos = this.position();
                Vec3 playerPos = getOwnerAnchor(player);
                Vec3 rope = playerPos.subtract(hookPos).normalize();
                Vec3 velocity = player.getDeltaMovement();
                double speed = velocity.length();

                Vec3 motion = speed > 1.0e-6D
                    ? velocity.scale(1.0D / speed).subtract(rope)
                    : Vec3.ZERO;

                double vx = clamp(motion.x * speed, -1.4D, 1.4D);
                double vy = motion.y * speed;
                if (vy > 1.0D)
                {
                    vy = 1.0D;
                }
                double vz = clamp(motion.z * speed, -1.4D, 1.4D);

                if (speed < 0.09D && distanceToOwner > ropeLength + 0.3F)
                {
                    vy = 0.1D;
                }

                player.setDeltaMovement(vx, vy, vz);
                player.hurtMarked = true;
            }
            else if (player.onGround() && distanceToOwner > ropeLength)
            {
                setRopeLength((float) distanceToOwner);
            }
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

        // Deal damage but bounce off (no capture for hook javelins)
        float damage = 4.0F;
        DamageSource source = this.damageSources().arrow(this, owner != null ? owner : this);
        target.hurt(source, damage);
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result)
    {
        super.onHitBlock(result);
        Entity owner = this.getOwner();
        if (owner != null)
        {
            setRopeLength((float) this.position().distanceTo(getOwnerAnchor(owner)));
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
        tag.putFloat("RopeLength", getRopeLength());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Weapon"))
        {
            setWeapon(ItemStack.parse(this.registryAccess(), tag.getCompound("Weapon")).orElse(ItemStack.EMPTY));
        }
        if (tag.contains("RopeLength"))
        {
            setRopeLength(tag.getFloat("RopeLength"));
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem()
    {
        return getWeapon();
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

    private boolean shouldDiscard(Entity owner)
    {
        if (owner == null || !owner.isAlive()) return true;
        if (!(owner instanceof LivingEntity living)) return true;

        return findLinkedThrownStack(living).isEmpty();
    }

    private void clearOwnerThrownState(Entity owner)
    {
        if (!(owner instanceof LivingEntity living))
        {
            return;
        }

        ItemStack linked = findLinkedThrownStack(living);
        if (!linked.isEmpty())
        {
            HookJavelinItem.clearThrownState(linked);
        }
    }

    private Vec3 getOwnerAnchor(Entity owner)
    {
        return owner.position();
    }

    private ItemStack findLinkedThrownStack(LivingEntity living)
    {
        ItemStack mainHand = living.getMainHandItem();
        if (HookJavelinItem.isLinkedThrownStack(mainHand, this.getUUID()))
        {
            return mainHand;
        }

        ItemStack offHand = living.getOffhandItem();
        if (HookJavelinItem.isLinkedThrownStack(offHand, this.getUUID()))
        {
            return offHand;
        }

        return ItemStack.EMPTY;
    }
}
