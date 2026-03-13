package com.rustysnail.terrafirmathings.common.blockentity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.block.SnareBlock;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SnareBlockEntity extends BlockEntity
{
    private static final String NBT_CAPTURED = "CapturedEntity";
    private static final String NBT_BAIT = "Bait";
    private static final String NBT_PASSIVE_TIMER = "PassiveTimer";

    private static final String TAG_PREV_NO_AI = "tfcthings_snare_prev_no_ai";
    private static final String TAG_PREV_INVULNERABLE = "tfcthings_snare_prev_invuln";

    @Nullable private UUID capturedEntityId;
    @Nullable private LivingEntity capturedEntityCache;
    private ItemStack bait = ItemStack.EMPTY;
    private int passiveTimer = 0;

    public SnareBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.SNARE.get(), pos, state);
    }

    public void serverTick()
    {
        if (level == null || level.isClientSide()) return;
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableSnare.get()) return;

        if (hasCapturedEntity())
        {
            LivingEntity captured = getCapturedEntity(level);
            if (captured == null || captured.isDeadOrDying())
            {
                clearCaptureState();
            }
            else
            {
                maintainCapture(captured);
            }
        }
        else if (!getBlockState().getValue(SnareBlock.TRIGGERED))
        {
            tickPassive();
        }
    }

    private void tickPassive()
    {
        if (!hasBait()) return;

        passiveTimer++;
        if (passiveTimer < TFCThingsConfig.ITEMS.SNARE.checkInterval.get()) return;
        passiveTimer = 0;

        if (!(level instanceof ServerLevel serverLevel)) return;
        if (level.random.nextDouble() >= TFCThingsConfig.ITEMS.SNARE.baseCatchChance.get()) return;

        attemptPassiveCatch(serverLevel);
    }

    private void attemptPassiveCatch(ServerLevel serverLevel)
    {
        EntityType<?> entityType = selectCatchableEntityType(serverLevel);

        Entity spawned = entityType.create(serverLevel);
        if (!(spawned instanceof LivingEntity livingEntity)) return;

        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY();
        double z = worldPosition.getZ() + 0.5;

        livingEntity.moveTo(x, y, z, serverLevel.random.nextFloat() * 360f, 0f);

        if (!serverLevel.addFreshEntity(livingEntity))
        {
            return;
        }

        if (beginCapture(livingEntity))
        {
            consumeBait();
            setBlockTriggered();
            level.playSound(null, worldPosition, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
        else
        {
            livingEntity.discard();
        }
    }

    /**
     * Picks a random entity type from the SNARE_CATCHABLE tag.
     * Falls back to CHICKEN if the tag is empty or missing.
     * TODO: Pick only mobs that are valid to spawn in that area
     */
    private EntityType<?> selectCatchableEntityType(ServerLevel serverLevel)
    {
        Iterable<Holder<EntityType<?>>> tagIterable = serverLevel.registryAccess()
            .registryOrThrow(Registries.ENTITY_TYPE)
            .getTagOrEmpty(TFCThingsTags.Entities.SNARE_CATCHABLE);
        List<Holder<EntityType<?>>> catchable = new ArrayList<>();
        tagIterable.forEach(catchable::add);

        if (catchable.isEmpty())
        {
            return EntityType.CHICKEN;
        }

        return catchable.get(serverLevel.random.nextInt(catchable.size())).value();
    }

    public boolean beginCapture(LivingEntity entity)
    {
        if (level == null || level.isClientSide()) return false;
        if (hasCapturedEntity()) return false;
        if (!isCatchable(entity)) return false;

        this.capturedEntityId = entity.getUUID();
        this.capturedEntityCache = entity;

        if (entity instanceof Mob mob)
        {
            mob.getPersistentData().putBoolean(TAG_PREV_NO_AI, mob.isNoAi());
            if (TFCThingsConfig.ITEMS.SNARE.holdNoAi.get())
            {
                mob.setNoAi(true);
            }
            mob.setPersistenceRequired();
            mob.getNavigation().stop();
            mob.setTarget(null);
        }

        entity.hurt(level.damageSources().generic(), 2.0f);

        if (TFCThingsConfig.ITEMS.SNARE.preventDamage.get())
        {
            entity.getPersistentData().putBoolean(TAG_PREV_INVULNERABLE, entity.isInvulnerable());
            entity.setInvulnerable(true);
        }

        entity.teleportTo(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5);
        entity.setDeltaMovement(0, 0, 0);
        entity.hurtMarked = true;

        markDirtyAndSync();
        return true;
    }

    private void maintainCapture(LivingEntity captured)
    {
        final double x = worldPosition.getX() + 0.5;
        final double z = worldPosition.getZ() + 0.5;

        final double dx = captured.getX() - x;
        final double dz = captured.getZ() - z;
        if ((dx * dx + dz * dz) > 0.0004)
        {
            captured.setPos(x, captured.getY(), z);
        }

        captured.setDeltaMovement(0, 0, 0);
        captured.hurtMarked = true;

        if (captured instanceof Mob mob)
        {
            if (TFCThingsConfig.ITEMS.SNARE.holdNoAi.get())
            {
                mob.setNoAi(true);
            }
            mob.setPersistenceRequired();
            mob.getNavigation().stop();
            mob.setTarget(null);
        }

        if (TFCThingsConfig.ITEMS.SNARE.preventDamage.get())
        {
            captured.setInvulnerable(true);
        }
    }

    public void releaseCapture()
    {
        if (level == null)
        {
            clearCaptureState();
            return;
        }

        LivingEntity captured = getCapturedEntity(level);

        if (captured instanceof Mob mob)
        {
            boolean prevNoAi = mob.getPersistentData().getBoolean(TAG_PREV_NO_AI);
            mob.setNoAi(prevNoAi);
            mob.getPersistentData().remove(TAG_PREV_NO_AI);
        }

        if (captured != null && captured.getPersistentData().contains(TAG_PREV_INVULNERABLE))
        {
            boolean prevInv = captured.getPersistentData().getBoolean(TAG_PREV_INVULNERABLE);
            captured.setInvulnerable(prevInv);
            captured.getPersistentData().remove(TAG_PREV_INVULNERABLE);
        }

        if (!bait.isEmpty() && level != null)
        {
            Containers.dropItemStack(level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5,
                bait.copy());
            bait = ItemStack.EMPTY;
        }

        passiveTimer = 0;
        clearCaptureState();
    }

    private void clearCaptureState()
    {
        this.capturedEntityId = null;
        this.capturedEntityCache = null;
        markDirtyAndSync();
    }

    public boolean canInsertBait(ItemStack stack)
    {
        return !stack.isEmpty() && !hasBait() && stack.is(TFCThingsTags.Items.SNARE_BAIT);
    }

    public void insertBait(ItemStack stack)
    {
        this.bait = stack.copyWithCount(1);
        stack.shrink(1);
        this.passiveTimer = 0;
        markDirtyAndSync();
    }

    public boolean hasBait()
    {
        return !bait.isEmpty();
    }

    public ItemStack getBait()
    {
        return bait;
    }

    public ItemStack removeBait()
    {
        ItemStack result = bait.copy();
        this.bait = ItemStack.EMPTY;
        this.passiveTimer = 0;
        markDirtyAndSync();
        return result;
    }

    private void consumeBait()
    {
        this.bait = ItemStack.EMPTY;
        this.passiveTimer = 0;
        markDirtyAndSync();
    }

    public boolean hasCapturedEntity()
    {
        return capturedEntityId != null;
    }

    @Nullable
    public LivingEntity getCapturedEntity(Level level)
    {
        if (capturedEntityId == null) return null;

        if (capturedEntityCache != null
            && capturedEntityCache.isAlive()
            && capturedEntityCache.getUUID().equals(capturedEntityId))
        {
            return capturedEntityCache;
        }

        if (level instanceof ServerLevel serverLevel)
        {
            Entity entity = serverLevel.getEntity(capturedEntityId);
            if (entity instanceof LivingEntity livingEntity)
            {
                capturedEntityCache = livingEntity;
                return livingEntity;
            }
        }
        return null;
    }

    private void setBlockTriggered()
    {
        if (level == null) return;
        BlockState state = getBlockState();
        if (state.hasProperty(SnareBlock.TRIGGERED) && !state.getValue(SnareBlock.TRIGGERED))
        {
            level.setBlock(worldPosition, state.setValue(SnareBlock.TRIGGERED, true), 3);
        }
    }

    private boolean isCatchable(LivingEntity entity)
    {
        return entity.getType().is(TFCThingsTags.Entities.SNARE_CATCHABLE);
    }

    private void markDirtyAndSync()
    {
        setChanged();
        if (level != null && !level.isClientSide())
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        capturedEntityId = tag.hasUUID(NBT_CAPTURED) ? tag.getUUID(NBT_CAPTURED) : null;
        capturedEntityCache = null;
        bait = tag.contains(NBT_BAIT)
            ? ItemStack.parseOptional(registries, tag.getCompound(NBT_BAIT))
            : ItemStack.EMPTY;
        passiveTimer = tag.getInt(NBT_PASSIVE_TIMER);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        if (capturedEntityId != null)
        {
            tag.putUUID(NBT_CAPTURED, capturedEntityId);
        }
        if (!bait.isEmpty())
        {
            tag.put(NBT_BAIT, bait.save(registries));
        }
        tag.putInt(NBT_PASSIVE_TIMER, passiveTimer);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }
}
