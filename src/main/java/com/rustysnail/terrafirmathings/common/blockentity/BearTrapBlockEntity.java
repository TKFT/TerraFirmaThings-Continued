package com.rustysnail.terrafirmathings.common.blockentity;

import java.util.UUID;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.rustysnail.terrafirmathings.common.TFCThingsTags;

public class BearTrapBlockEntity extends BlockEntity
{
    private static final String NBT_CAPTURED = "CapturedEntity";

    @Nullable
    private UUID capturedEntityId;
    @Nullable
    private LivingEntity capturedEntityCache;

    public BearTrapBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.BEAR_TRAP.get(), pos, state);
    }

    public void serverTick()
    {
        if (level == null || level.isClientSide())
        {
            return;
        }
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableBearTrap.get())
        {
            return;
        }

        LivingEntity captured = getCapturedEntity(level);
        if (captured == null)
        {
            if (hasCapturedEntity())
            {
                setCapturedEntity(null);
            }
            return;
        }

        if (captured.isDeadOrDying())
        {
            setCapturedEntity(null);
            return;
        }

        if (captured.getType().is(TFCThingsTags.Entities.BEAR_TRAP_BREAKOUT))
        {
            int breakoutPercent = TFCThingsConfig.ITEMS.BEAR_TRAP.breakoutChance.get();
            if (breakoutPercent > 0 && level.random.nextDouble() < breakoutPercent / 100.0 / 24000.0)
            {
                breakTrap();
                return;
            }
        }

        double trapX = worldPosition.getX() + 0.5;
        double trapY = worldPosition.getY();
        double trapZ = worldPosition.getZ() + 0.5;

        double distSq = captured.distanceToSqr(trapX, trapY, trapZ);
        if (distSq > 1.0E-6)
        {
            captured.teleportTo(trapX, trapY, trapZ);
        }

        captured.setDeltaMovement(0, 0, 0);
        if (captured instanceof Mob mob)
        {
            mob.getNavigation().stop();
        }
        captured.hurtMarked = true;
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
        markDirtyAndSync();
    }

    private void markDirtyAndSync()
    {
        setChanged();
        if (level != null && !level.isClientSide())
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasCapturedEntity()
    {
        return capturedEntityId != null;
    }

    public void reset()
    {
        setCapturedEntity(null);
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

    private void breakTrap()
    {
        if (level == null) return;

        level.playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);

        double breakChance = TFCThingsConfig.ITEMS.BEAR_TRAP.breakChance.get();
        double dropChance = Math.max(0.0, 1.0 - 2.0 * breakChance);
        if (level.random.nextDouble() < dropChance)
        {
            Block.popResource(level, worldPosition, new ItemStack(TFCThingsItems.BEAR_TRAP.get()));
        }

        level.removeBlock(worldPosition, false);
    }
}
