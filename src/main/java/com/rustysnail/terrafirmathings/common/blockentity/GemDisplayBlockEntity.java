package com.rustysnail.terrafirmathings.common.blockentity;

import java.util.ArrayList;
import java.util.List;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GemDisplayBlockEntity extends BlockEntity
{
    public static final int MAX_GEMS = 3;

    private static final String NBT_GEMS = "Gems";

    private final NonNullList<ItemStack> gems = NonNullList.withSize(MAX_GEMS, ItemStack.EMPTY);
    private final List<ItemStack> overflowGems = new ArrayList<>();

    public GemDisplayBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.GEM_DISPLAY.get(), pos, state);
    }

    public ItemStack getGem(int slot)
    {
        return gems.get(slot);
    }

    public int getGemCount()
    {
        int count = 0;
        for (ItemStack stack : gems)
        {
            if (!stack.isEmpty()) count++;
        }
        return count;
    }

    public boolean isFull()
    {
        return getGemCount() >= MAX_GEMS;
    }

    public boolean addGem(ItemStack stack)
    {
        for (int i = 0; i < MAX_GEMS; i++)
        {
            if (gems.get(i).isEmpty())
            {
                gems.set(i, stack.copyWithCount(1));
                markUpdated();
                return true;
            }
        }
        return false;
    }

    public ItemStack removeLastGem()
    {
        for (int i = MAX_GEMS - 1; i >= 0; i--)
        {
            if (!gems.get(i).isEmpty())
            {
                ItemStack removed = gems.get(i).copy();
                gems.set(i, ItemStack.EMPTY);
                markUpdated();
                return removed;
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isGemEligible(ItemStack stack)
    {
        if (stack.isEmpty()) return false;
        return stack.is(TFCThingsTags.Items.GEM_DISPLAY_ELIGIBLE);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (level != null && !level.isClientSide() && !overflowGems.isEmpty())
        {
            for (ItemStack stack : overflowGems)
            {
                Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, stack);
            }
            overflowGems.clear();
            setChanged();
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        overflowGems.clear();
        for (int i = 0; i < MAX_GEMS; i++)
        {
            gems.set(i, ItemStack.EMPTY);
        }
        if (tag.contains(NBT_GEMS, Tag.TAG_LIST))
        {
            ListTag listTag = tag.getList(NBT_GEMS, Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++)
            {
                CompoundTag entryTag = listTag.getCompound(i);
                if (!entryTag.isEmpty())
                {
                    ItemStack parsed = ItemStack.parse(registries, entryTag).orElse(ItemStack.EMPTY);
                    if (!parsed.isEmpty())
                    {
                        if (i < MAX_GEMS)
                        {
                            gems.set(i, parsed);
                        }
                        else
                        {
                            overflowGems.add(parsed);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        ListTag listTag = new ListTag();
        for (ItemStack stack : gems)
        {
            listTag.add(stack.isEmpty() ? new CompoundTag() : stack.save(registries));
        }
        tag.put(NBT_GEMS, listTag);
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

    private void markUpdated()
    {
        setChanged();
        if (level != null && !level.isClientSide())
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
