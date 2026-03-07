package com.rustysnail.terrafirmathings.common.blockentity;

import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RopeBridgeBlockEntity extends BlockEntity
{

    private static final String NBT_LAST_STEP_TICK = "LastStepTick";
    private static final String NBT_STEP_STRENGTH = "StepStrength";

    private long lastStepTick = -1;
    private float stepStrength = 0f;

    public RopeBridgeBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.ROPE_BRIDGE.get(), pos, state);
    }

    public void recordStepImpulse(long gameTime, float strength)
    {
        this.lastStepTick = gameTime;
        this.stepStrength = Math.max(this.stepStrength, strength);
        if (level != null && !level.isClientSide())
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public long getLastStepTick() {return lastStepTick;}

    public float getStepStrength() {return stepStrength;}

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        lastStepTick = tag.contains(NBT_LAST_STEP_TICK) ? tag.getLong(NBT_LAST_STEP_TICK) : -1;
        stepStrength = tag.contains(NBT_STEP_STRENGTH) ? tag.getFloat(NBT_STEP_STRENGTH) : 0f;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putLong(NBT_LAST_STEP_TICK, lastStepTick);
        tag.putFloat(NBT_STEP_STRENGTH, stepStrength);
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
        tag.putLong(NBT_LAST_STEP_TICK, lastStepTick);
        tag.putFloat(NBT_STEP_STRENGTH, stepStrength);
        return tag;
    }
}
