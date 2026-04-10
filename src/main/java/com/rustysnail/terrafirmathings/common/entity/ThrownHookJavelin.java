package com.rustysnail.terrafirmathings.common.entity;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.item.HookJavelinItem;
import com.rustysnail.terrafirmathings.common.util.SharpnessHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.items.JavelinItem;

public class ThrownHookJavelin extends AbstractArrow implements ItemSupplier
{

    public enum HookState
    {
        FLYING,
        ANCHORED,
        RETRACTING;

        public static HookState fromInt(int i)
        {
            HookState[] values = values();
            return (i >= 0 && i < values.length) ? values[i] : FLYING;
        }
    }

    public static final float MIN_ROPE_LENGTH = 1.0F;

    private static final double ROPE_CORRECTION_SPEED = 0.12;

    private static final double ROPE_CORRECTION_MAX = 0.4;

    private static final double RETRACT_STEP_SPEED = 1.5;

    private static final EntityDataAccessor<ItemStack> DATA_WEAPON =
        SynchedEntityData.defineId(ThrownHookJavelin.class, EntityDataSerializers.ITEM_STACK);

    private static final EntityDataAccessor<Float> DATA_ROPE_LENGTH =
        SynchedEntityData.defineId(ThrownHookJavelin.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> DATA_STATE =
        SynchedEntityData.defineId(ThrownHookJavelin.class, EntityDataSerializers.INT);

    @Nullable
    private BlockPos anchorPos = null;

    @Nullable
    private Direction anchorFace = null;

    @Nullable
    private Vec3 anchorPoint = null;


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
        this.entityData.set(DATA_ROPE_LENGTH, Math.max(MIN_ROPE_LENGTH, Math.min(length, maxLength)));
    }

    public void retractRope(float amount)
    {
        setRopeLength(getRopeLength() - amount);
    }

    // TODO: Add extending ability to hook javelin
    public void extendRope(float amount)
    {
        setRopeLength(getRopeLength() + amount);
    }

    public HookState getState()
    {
        return HookState.fromInt(this.entityData.get(DATA_STATE));
    }

    private void setState(HookState state)
    {
        this.entityData.set(DATA_STATE, state.ordinal());
    }

    public boolean isFlying()
    {
        return getState() == HookState.FLYING;
    }

    public boolean isAnchored()
    {
        return getState() == HookState.ANCHORED;
    }

    public void startAutoRetract()
    {
        setState(HookState.RETRACTING);
        this.setNoGravity(true);
        this.setNoPhysics(true);
        this.inGround = false;
        this.setDeltaMovement(Vec3.ZERO);
        this.anchorPos = null;
        this.anchorFace = null;
        this.anchorPoint = null;
    }

    private boolean canAnchorTo(Level level, BlockPos pos, BlockState state, Direction face)
    {
        if (!state.getFluidState().isEmpty()) return false;
        if (state.canBeReplaced()) return false;
        if (!state.isFaceSturdy(level, pos, face, SupportType.CENTER)) return false;
        return !state.is(TFCThingsTags.Blocks.UNHOOKABLE);
    }

    private void enterAnchoredState(BlockHitResult result)
    {
        this.anchorPos = result.getBlockPos();
        this.anchorFace = result.getDirection();
        this.anchorPoint = result.getLocation();
        setState(HookState.ANCHORED);

        Entity owner = this.getOwner();
        if (owner != null)
        {
            Vec3 ownerTether = owner instanceof Player p
                ? getPlayerTetherPoint(p)
                : owner.position().add(0.0, owner.getBbHeight() * 0.6, 0.0);
            setRopeLength((float) result.getLocation().distanceTo(ownerTether));
        }

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.TRIDENT_HIT_GROUND, SoundSource.PLAYERS, 0.8F, 1.2F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_WEAPON, ItemStack.EMPTY);
        builder.define(DATA_ROPE_LENGTH, 0.0F);
        builder.define(DATA_STATE, HookState.FLYING.ordinal());
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

        switch (getState())
        {
            case FLYING -> tickFlying(owner);
            case ANCHORED -> tickAnchored(owner);
            case RETRACTING -> tickRetracting(owner);
        }
    }

    private void tickFlying(@Nullable Entity owner)
    {
        if (shouldDiscard(owner))
        {
            clearOwnerThrownState(owner);
            this.discard();
        }
    }

    private void tickAnchored(@Nullable Entity owner)
    {
        if (shouldDiscard(owner))
        {
            clearOwnerThrownState(owner);
            this.discard();
            return;
        }

        if (anchorPos != null && anchorFace != null)
        {
            BlockState current = this.level().getBlockState(anchorPos);
            if (!canAnchorTo(this.level(), anchorPos, current, anchorFace))
            {
                startAutoRetract();
                return;
            }
        }

        if (!(owner instanceof Player player))
        {
            return;
        }

        Vec3 anchorPt = getEffectiveAnchorPoint();
        Vec3 tetherPt = getPlayerTetherPoint(player);
        double dist = anchorPt.distanceTo(tetherPt);

        float maxLength = TFCThingsConfig.ITEMS.HOOK_JAVELIN.maxRopeLength.get().floatValue();
        if (dist > maxLength)
        {
            startAutoRetract();
            return;
        }

        applyRopeConstraint(player, anchorPt, tetherPt, dist);
    }

    private void tickRetracting(@Nullable Entity owner)
    {
        if (owner == null)
        {
            this.discard();
            return;
        }

        this.setNoGravity(true);
        this.setNoPhysics(true);
        this.inGround = false;

        Vec3 target = owner.position();
        Vec3 toTarget = target.subtract(this.position());
        double dist = toTarget.length();

        if (dist < 1.0)
        {
            clearOwnerThrownState(owner);
            this.discard();
            return;
        }

        Vec3 step = toTarget.normalize().scale(Math.min(RETRACT_STEP_SPEED, dist));
        Vec3 next = this.position().add(step);
        this.setPos(next.x, next.y, next.z);
        this.setDeltaMovement(Vec3.ZERO);
    }

    private Vec3 getPlayerTetherPoint(LivingEntity entity)
    {
        return entity.position().add(0.0, entity.getBbHeight() * 0.6, 0.0);
    }

    private Vec3 getEffectiveAnchorPoint()
    {
        return anchorPoint != null ? anchorPoint : this.position();
    }

    private void applyRopeConstraint(Player player, Vec3 anchorPt, Vec3 tetherPt, double dist)
    {
        float ropeLength = getRopeLength();
        if (dist <= ropeLength)
        {
            return;
        }

        Vec3 radial = tetherPt.subtract(anchorPt).normalize();
        Vec3 velocity = player.getDeltaMovement();
        double radialSpeed = velocity.dot(radial);

        Vec3 constrained;
        if (radialSpeed > 0.0)
        {
            constrained = velocity.subtract(radial.scale(radialSpeed));
        }
        else
        {
            constrained = velocity;
        }

        double overlap = dist - ropeLength;
        double correction = Math.min(overlap * ROPE_CORRECTION_SPEED, ROPE_CORRECTION_MAX);
        constrained = constrained.add(radial.scale(-correction));

        player.setDeltaMovement(constrained);
        player.hurtMarked = true;

        if (radialSpeed > 0.0 && velocity.y < 0.0)
        {
            player.fallDistance = 0.0F;
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

        ItemStack weapon = getWeapon();
        float baseDamage = weapon.getItem() instanceof JavelinItem jav ? jav.getThrownDamage() : 4.0F;
        float damage = baseDamage + SharpnessHelper.getDamageBonusForThrown(weapon);
        DamageSource source = this.damageSources().arrow(this, owner != null ? owner : this);
        boolean hurt = target.hurt(source, damage);
        if (hurt)
        {
            consumeSharpnessFromOwnerItem(owner);
        }
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result)
    {
        super.onHitBlock(result);

        if (this.level().isClientSide())
        {
            return;
        }

        if (!isFlying())
        {
            return;
        }

        BlockPos pos = result.getBlockPos();
        Direction face = result.getDirection();
        BlockState state = this.level().getBlockState(pos);

        if (canAnchorTo(this.level(), pos, state, face))
        {
            enterAnchoredState(result);
        }
        else
        {
            startAutoRetract();
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
        tag.putInt("HookState", getState().ordinal());
        if (anchorPos != null)
        {
            tag.putLong("AnchorPos", anchorPos.asLong());
        }
        if (anchorFace != null)
        {
            tag.putInt("AnchorFace", anchorFace.get3DDataValue());
        }
        if (anchorPoint != null)
        {
            CompoundTag pt = new CompoundTag();
            pt.putDouble("x", anchorPoint.x);
            pt.putDouble("y", anchorPoint.y);
            pt.putDouble("z", anchorPoint.z);
            tag.put("AnchorPoint", pt);
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
        if (tag.contains("RopeLength"))
        {
            setRopeLength(tag.getFloat("RopeLength"));
        }
        if (tag.contains("HookState"))
        {
            setState(HookState.fromInt(tag.getInt("HookState")));
        }
        if (tag.contains("AnchorPos"))
        {
            this.anchorPos = BlockPos.of(tag.getLong("AnchorPos"));
        }
        if (tag.contains("AnchorFace"))
        {
            this.anchorFace = Direction.from3DDataValue(tag.getInt("AnchorFace"));
        }
        if (tag.contains("AnchorPoint"))
        {
            CompoundTag pt = tag.getCompound("AnchorPoint");
            this.anchorPoint = new Vec3(pt.getDouble("x"), pt.getDouble("y"), pt.getDouble("z"));
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem()
    {
        return getWeapon();
    }

    private boolean shouldDiscard(@Nullable Entity owner)
    {
        if (owner == null || !owner.isAlive()) return true;
        if (!(owner instanceof LivingEntity living)) return true;
        return findLinkedThrownStack(living).isEmpty();
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
            HookJavelinItem.clearThrownState(linked);
        }
    }

    private void consumeSharpnessFromOwnerItem(@Nullable Entity owner)
    {
        if (!(owner instanceof LivingEntity living)) return;
        ItemStack linked = findLinkedThrownStack(living);
        if (!linked.isEmpty())
        {
            SharpnessHelper.consumeCharge(linked);
        }
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
