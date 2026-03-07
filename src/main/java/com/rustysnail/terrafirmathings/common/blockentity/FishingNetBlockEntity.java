package com.rustysnail.terrafirmathings.common.blockentity;

import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FishingNetBlockEntity extends BlockEntity
{

    private static final String TAG_ANCHOR_A = "AnchorA";
    private static final String TAG_ANCHOR_B = "AnchorB";

    @Nullable
    private BlockPos anchorA = null;
    @Nullable
    private BlockPos anchorB = null;

    public FishingNetBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.FISHING_NET.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        anchorA = tag.contains(TAG_ANCHOR_A) ? BlockPos.of(tag.getLong(TAG_ANCHOR_A)) : null;
        anchorB = tag.contains(TAG_ANCHOR_B) ? BlockPos.of(tag.getLong(TAG_ANCHOR_B)) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        if (anchorA != null)
        {
            tag.putLong(TAG_ANCHOR_A, anchorA.asLong());
        }
        if (anchorB != null)
        {
            tag.putLong(TAG_ANCHOR_B, anchorB.asLong());
        }
    }
}
