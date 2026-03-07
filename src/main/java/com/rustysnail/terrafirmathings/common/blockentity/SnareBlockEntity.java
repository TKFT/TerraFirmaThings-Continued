package com.rustysnail.terrafirmathings.common.blockentity;

import java.util.UUID;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SnareBlockEntity extends BlockEntity
{

    private static final String NBT_CAPTURED = "CapturedEntity";

    private static final String TAG_PREV_NO_AI = "tfcthings_snare_prev_no_ai";
    private static final String TAG_PREV_INVULNERABLE = "tfcthings_snare_prev_invuln";

    @Nullable private UUID capturedEntityId;
    @Nullable private LivingEntity capturedEntityCache;

    public SnareBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.SNARE.get(), pos, state);
    }

    public void serverTick()
    {
        if (level == null || level.isClientSide()) return;
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableSnare.get()) return;

        LivingEntity captured = getCapturedEntity(level);
        if (captured != null)
        {
            if (captured.isDeadOrDying())
            {
                setCapturedEntity(null);
                return;
            }

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
        else if (hasCapturedEntity())
        {
            setCapturedEntity(null);
        }
    }

    public boolean tryCapture(LivingEntity entity)
    {
        if (level == null || level.isClientSide()) return false;
        if (hasCapturedEntity()) return false;
        if (!isCatchable(entity)) return false;

        setCapturedEntity(entity);

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

        if (TFCThingsConfig.ITEMS.SNARE.preventDamage.get())
        {
            entity.getPersistentData().putBoolean(TAG_PREV_INVULNERABLE, entity.isInvulnerable());
            entity.setInvulnerable(true);
        }

        entity.teleportTo(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5);
        entity.setDeltaMovement(0, 0, 0);
        entity.hurtMarked = true;

        setChanged();
        return true;
    }

    public boolean hasCapturedEntity()
    {
        return capturedEntityId != null;
    }

    @Nullable
    public LivingEntity getCapturedEntity(Level level)
    {
        if (capturedEntityId == null)
        {
            return null;
        }

        if (capturedEntityCache != null && capturedEntityCache.isAlive() && capturedEntityCache.getUUID().equals(capturedEntityId))
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

    public void setCapturedEntity(@Nullable LivingEntity entity)
    {
        if (entity != null)
        {
            this.capturedEntityId = entity.getUUID();
            this.capturedEntityCache = entity;
        }
        else
        {
            this.capturedEntityId = null;
            this.capturedEntityCache = null;
        }

        setChanged();
        if (level != null && !level.isClientSide())
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void release()
    {
        if (level == null)
        {
            setCapturedEntity(null);
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

        setCapturedEntity(null);
        setChanged();
    }

    public void reset()
    {
        release();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID(NBT_CAPTURED))
        {
            capturedEntityId = tag.getUUID(NBT_CAPTURED);
        }
        else
        {
            capturedEntityId = null;
        }
        capturedEntityCache = null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        if (capturedEntityId != null)
        {
            tag.putUUID(NBT_CAPTURED, capturedEntityId);
        }
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

    private boolean isCatchable(LivingEntity entity)
    {
        return entity.getType().is(TFCThingsTags.Entities.SNARE_CATCHABLE);
    }
}

