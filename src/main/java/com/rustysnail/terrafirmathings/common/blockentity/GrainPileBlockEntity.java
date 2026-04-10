package com.rustysnail.terrafirmathings.common.blockentity;

import java.util.ArrayList;
import java.util.List;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsFoodTraits;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.block.GrainPileBlock;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;

public class GrainPileBlockEntity extends BlockEntity
{

    private record Cohort(long creationDate, int count) {}

    private static final String NBT_GRAIN = "Grain";
    private static final String NBT_COHORTS = "Cohorts";
    private static final String NBT_COHORT_DATE = "Date";
    private static final String NBT_COHORT_COUNT = "Count";
    private static final String NBT_SHELTERED = "Sheltered";
    private static final int MAX_COHORTS = 128;

    private static final int ABSORB_INTERVAL = 10;
    private static final int GRAVITY_INTERVAL = 20;
    private static final int SHELTER_CHECK_INTERVAL = 20;
    private static final int PRESERVATION_INTERVAL = 1200;
    private static final int MAX_FLOW_DEPTH = 16;

    private ItemStack grainType = ItemStack.EMPTY;
    private final List<Cohort> cohorts = new ArrayList<>();
    private int totalCount = 0;
    private boolean sheltered = false;

    @Nullable
    private IItemHandler itemHandler;

    public GrainPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.GRAIN_PILE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos ignoredPos, BlockState ignoredState, GrainPileBlockEntity be)
    {
        if (level.isClientSide()) return;
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableGrainPiles.get()) return;

        long t = level.getGameTime();

        if (t % ABSORB_INTERVAL == 0) be.absorbNearbyGrainItems();
        if (t % GRAVITY_INTERVAL == 0) be.tryPullFromAbove();
        if (t % SHELTER_CHECK_INTERVAL == 0) be.checkShelterState();
        if (t % PRESERVATION_INTERVAL == 0) be.updateSentinel();
    }

    public boolean isEmpty() {return totalCount == 0 || grainType.isEmpty();}

    public boolean isFull() {return totalCount >= getMaxCapacity();}

    public int getCount() {return totalCount;}

    public int getMaxCapacity() {return TFCThingsConfig.ITEMS.GRAIN_PILE.maxCapacity.get();}

    public ItemStack getGrainType()
    {
        return grainType.isEmpty() ? ItemStack.EMPTY : grainType.copyWithCount(1);
    }

    public boolean canInsertGrain(ItemStack stack)
    {
        if (stack.isEmpty()) return false;
        if (!stack.is(TFCThingsTags.Items.GRAIN_PILE_ITEMS)) return false;
        if (totalCount >= getMaxCapacity()) return false;
        return grainType.isEmpty() || ItemStack.isSameItem(grainType, stack);
    }

    public int insertGrain(ItemStack stack)
    {
        if (!canInsertGrain(stack)) return 0;

        if (grainType.isEmpty())
            grainType = stack.copyWithCount(1);

        int space = getMaxCapacity() - totalCount;
        int toInsert = Math.min(space, stack.getCount());
        if (toInsert <= 0) return 0;

        long adjustedDate = computeInsertionDate(stack);
        long bucketDate = roundDate(adjustedDate);
        addCohort(bucketDate, toInsert);
        stack.shrink(toInsert);

        updateSentinel();
        setChanged();
        return toInsert;
    }

    public ItemStack extractGrain(int amount)
    {
        if (isEmpty())
            pullFromAbove(ItemStack.EMPTY, amount, MAX_FLOW_DEPTH);
        else if (totalCount < amount)
            pullFromAbove(grainType, amount - totalCount, MAX_FLOW_DEPTH);

        if (isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, totalCount);
        long oldestDate = cohorts.getFirst().creationDate();

        ItemStack result = grainType.copyWithCount(toExtract);
        FoodCapability.setCreationDate(result, oldestDate);

        drainCohorts(toExtract);

        FoodCapability.removeTrait(result, TFCThingsFoodTraits.GRAIN_PILE);
        FoodCapability.removeTrait(result, TFCThingsFoodTraits.GRAIN_PILE_SHELTERED);

        if (!isEmpty()) updateSentinel();
        setChanged();
        return result;
    }

    private void drainCohorts(int amount)
    {
        int remaining = amount;
        while (remaining > 0 && !cohorts.isEmpty())
        {
            Cohort oldest = cohorts.getFirst();
            int take = Math.min(remaining, oldest.count());
            if (take == oldest.count())
                cohorts.removeFirst();
            else
                cohorts.set(0, new Cohort(oldest.creationDate(), oldest.count() - take));
            remaining -= take;
        }
        totalCount -= amount;
        if (totalCount <= 0)
        {
            totalCount = 0;
            grainType = ItemStack.EMPTY;
            cohorts.clear();
        }
    }

    private void addCohort(long date, int count)
    {
        for (int i = 0; i < cohorts.size(); i++)
        {
            Cohort c = cohorts.get(i);
            if (c.creationDate() == date)
            {
                cohorts.set(i, new Cohort(date, c.count() + count));
                totalCount += count;
                return;
            }
            if (compareDates(date, c.creationDate()) < 0)
            {
                cohorts.add(i, new Cohort(date, count));
                totalCount += count;
                return;
            }
        }
        cohorts.add(new Cohort(date, count));
        totalCount += count;

        while (cohorts.size() > MAX_COHORTS)
        {
            int last = cohorts.size() - 1;
            Cohort b = cohorts.remove(last);
            Cohort a = cohorts.get(last - 1);
            cohorts.set(last - 1, new Cohort(a.creationDate(), a.count() + b.count()));
        }
    }

    private static long roundDate(long date)
    {
        if (date == IFood.ROTTEN_FLAG
            || date == IFood.NEVER_DECAY_FLAG
            || date == IFood.INVISIBLE_NEVER_DECAY_FLAG
            || date == IFood.TRANSIENT_NEVER_DECAY_FLAG)
        {
            return date;
        }
        return FoodCapability.getRoundedCreationDate(date);
    }

    private static int compareDates(long a, long b)
    {
        if (a == b) return 0;
        if (a == IFood.ROTTEN_FLAG) return -1;
        if (b == IFood.ROTTEN_FLAG) return 1;
        if (a < 0) return 1;
        if (b < 0) return -1;
        return Long.compare(a, b);
    }

    private long computeInsertionDate(ItemStack stack)
    {
        ItemStack temp = stack.copyWithCount(1);
        FoodCapability.removeTrait(temp, TFCThingsFoodTraits.GRAIN_PILE);
        FoodCapability.removeTrait(temp, TFCThingsFoodTraits.GRAIN_PILE_SHELTERED);
        FoodCapability.applyTrait(temp, TFCThingsFoodTraits.activeTraitFor(sheltered));
        IFood food = FoodCapability.get(temp);
        return food != null ? food.getCreationDate() : IFood.NEVER_DECAY_FLAG;
    }

    private void transferCohortDates(boolean oldSheltered, boolean newSheltered)
    {
        if (grainType.isEmpty()) return;

        for (int i = 0; i < cohorts.size(); i++)
        {
            ItemStack temp = grainType.copyWithCount(1);
            FoodCapability.removeTrait(temp, TFCThingsFoodTraits.GRAIN_PILE);
            FoodCapability.removeTrait(temp, TFCThingsFoodTraits.GRAIN_PILE_SHELTERED);
            FoodCapability.applyTrait(temp, TFCThingsFoodTraits.activeTraitFor(oldSheltered));
            FoodCapability.setCreationDate(temp, cohorts.get(i).creationDate());

            FoodCapability.removeTrait(temp, TFCThingsFoodTraits.activeTraitFor(oldSheltered));
            FoodCapability.applyTrait(temp, TFCThingsFoodTraits.activeTraitFor(newSheltered));

            IFood food = FoodCapability.get(temp);
            long newDate = (food != null) ? food.getCreationDate() : IFood.NEVER_DECAY_FLAG;
            cohorts.set(i, new Cohort(roundDate(newDate), cohorts.get(i).count()));
        }

        cohorts.sort((a, b) -> compareDates(a.creationDate(), b.creationDate()));
        for (int i = 0; i < cohorts.size() - 1; )
        {
            if (cohorts.get(i).creationDate() == cohorts.get(i + 1).creationDate())
            {
                cohorts.set(i, new Cohort(cohorts.get(i).creationDate(),
                    cohorts.get(i).count() + cohorts.get(i + 1).count()));
                cohorts.remove(i + 1);
            }
            else i++;
        }
    }

    private void tryPullFromAbove()
    {
        if (level == null || level.isClientSide()) return;
        int space = getMaxCapacity() - totalCount;
        if (space <= 0) return;
        int before = totalCount;
        pullFromAbove(grainType, space, 1);
        if (totalCount != before)
            syncBlockState();
    }

    private void pullFromAbove(ItemStack typeToMatch, int needed, int depth)
    {
        if (depth <= 0 || needed <= 0 || level == null || level.isClientSide()) return;

        BlockPos above = worldPosition.above();
        if (!(level.getBlockState(above).getBlock() instanceof GrainPileBlock)) return;
        if (!(level.getBlockEntity(above) instanceof GrainPileBlockEntity abovePile)) return;
        if (abovePile.isEmpty()) return;
        if (!typeToMatch.isEmpty() && !ItemStack.isSameItem(typeToMatch, abovePile.grainType)) return;

        if (abovePile.totalCount < needed && depth > 1)
            abovePile.pullFromAbove(abovePile.grainType, needed - abovePile.totalCount, depth - 1);

        int space = getMaxCapacity() - totalCount;
        int toMove = Math.min(Math.min(needed, abovePile.totalCount), space);
        if (toMove <= 0) return;

        if (this.grainType.isEmpty())
            this.grainType = abovePile.grainType.copyWithCount(1);

        boolean needsConversion = (abovePile.sheltered != this.sheltered);

        int remaining = toMove;
        while (remaining > 0 && !abovePile.cohorts.isEmpty())
        {
            Cohort oldest = abovePile.cohorts.getFirst();
            int take = Math.min(remaining, oldest.count());

            long date = needsConversion
                ? convertDate(abovePile.grainType, oldest.creationDate(), abovePile.sheltered, this.sheltered)
                : oldest.creationDate();
            addCohort(date, take);

            if (take == oldest.count())
                abovePile.cohorts.removeFirst();
            else
                abovePile.cohorts.set(0, new Cohort(oldest.creationDate(), oldest.count() - take));
            abovePile.totalCount -= take;
            remaining -= take;
        }

        if (abovePile.totalCount <= 0)
        {
            abovePile.totalCount = 0;
            abovePile.grainType = ItemStack.EMPTY;
            abovePile.cohorts.clear();
        }

        abovePile.setChanged();
        abovePile.syncBlockState();
    }

    private long convertDate(ItemStack typeStack, long adjustedDate, boolean fromSheltered, boolean toSheltered)
    {
        ItemStack temp = typeStack.copyWithCount(1);
        FoodCapability.removeTrait(temp, TFCThingsFoodTraits.GRAIN_PILE);
        FoodCapability.removeTrait(temp, TFCThingsFoodTraits.GRAIN_PILE_SHELTERED);
        FoodCapability.applyTrait(temp, TFCThingsFoodTraits.activeTraitFor(fromSheltered));
        FoodCapability.setCreationDate(temp, adjustedDate);
        FoodCapability.removeTrait(temp, TFCThingsFoodTraits.activeTraitFor(fromSheltered));
        FoodCapability.applyTrait(temp, TFCThingsFoodTraits.activeTraitFor(toSheltered));
        IFood food = FoodCapability.get(temp);
        return food != null ? roundDate(food.getCreationDate()) : adjustedDate;
    }

    private void absorbNearbyGrainItems()
    {
        if (level == null || level.isClientSide()) return;

        AABB searchBox = new AABB(
            worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
            worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1);

        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, searchBox))
        {
            if (itemEntity.isRemoved()) continue;
            ItemStack itemStack = itemEntity.getItem();
            if (!itemStack.is(TFCThingsTags.Items.GRAIN_PILE_ITEMS)) continue;
            if (!grainType.isEmpty() && !ItemStack.isSameItem(grainType, itemStack)) continue;

            if (totalCount < getMaxCapacity())
            {
                insertGrain(itemStack);
                syncBlockState();
                if (itemStack.isEmpty()) itemEntity.discard();
            }
            else
            {
                BlockPos above = worldPosition.above();
                BlockState aboveState = level.getBlockState(above);

                if (aboveState.isAir())
                {
                    int inserted = GrainPileBlock.formPile(level, above, itemStack,
                        (GrainPileBlock) getBlockState().getBlock());
                    if (inserted > 0)
                    {
                        itemStack.shrink(inserted);
                        if (itemStack.isEmpty()) itemEntity.discard();
                    }
                }
                else if (level.getBlockEntity(above) instanceof GrainPileBlockEntity abovePile
                    && abovePile.canInsertGrain(itemStack))
                {
                    abovePile.insertGrain(itemStack);
                    abovePile.syncBlockState();
                    if (itemStack.isEmpty()) itemEntity.discard();
                }
            }
        }
    }

    private void checkShelterState()
    {
        if (level == null || level.isClientSide() || grainType.isEmpty() || cohorts.isEmpty()) return;

        boolean newSheltered = level.getBrightness(LightLayer.SKY, worldPosition) < 14;
        if (newSheltered == this.sheltered) return;

        transferCohortDates(this.sheltered, newSheltered);
        this.sheltered = newSheltered;
        updateSentinel();
        setChanged();
    }

    private void updateSentinel()
    {
        if (grainType.isEmpty() || cohorts.isEmpty()) return;

        FoodCapability.removeTrait(grainType, TFCThingsFoodTraits.GRAIN_PILE);
        FoodCapability.removeTrait(grainType, TFCThingsFoodTraits.GRAIN_PILE_SHELTERED);
        FoodCapability.applyTrait(grainType, TFCThingsFoodTraits.activeTraitFor(sheltered));
        FoodCapability.setCreationDate(grainType, cohorts.getFirst().creationDate());
    }

    public void syncBlockState()
    {
        if (level == null || level.isClientSide()) return;
        setChanged();

        if (totalCount == 0)
        {
            level.removeBlock(worldPosition, false);
            return;
        }

        BlockState current = getBlockState();
        if (!current.hasProperty(GrainPileBlock.LAYERS)) return;

        int tier = getFullnessTier();
        BlockState newState = current.setValue(GrainPileBlock.LAYERS, tier);
        if (!newState.equals(current))
            level.setBlock(worldPosition, newState, 3);
        else
            level.sendBlockUpdated(worldPosition, current, current, 3);
    }

    public int getFullnessTier()
    {
        int max = getMaxCapacity();
        if (totalCount <= 0) return 1;
        return Math.max(1, Math.min(8, (int) Math.ceil((double) totalCount / max * 8)));
    }

    public IItemHandler getItemHandler()
    {
        if (itemHandler == null) itemHandler = new GrainPileItemHandler();
        return itemHandler;
    }

    private class GrainPileItemHandler implements IItemHandler
    {
        @Override
        public int getSlots() {return 1;}

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            if (slot != 0 || isEmpty()) return ItemStack.EMPTY;
            return grainType.copyWithCount(totalCount);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            if (slot != 0 || !canInsertGrain(stack)) return stack;
            int space = getMaxCapacity() - totalCount;
            int toInsert = Math.min(space, stack.getCount());
            if (toInsert <= 0) return stack;

            if (!simulate)
            {
                ItemStack temp = stack.copyWithCount(toInsert);
                insertGrain(temp);
                syncBlockState();
            }
            return stack.copyWithCount(stack.getCount() - toInsert);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (slot != 0) return ItemStack.EMPTY;
            if (simulate)
            {
                if (!isEmpty())
                    return grainType.copyWithCount(Math.min(amount, totalCount));
                if (level == null) return ItemStack.EMPTY;
                BlockPos above = worldPosition.above();
                if (level.getBlockEntity(above) instanceof GrainPileBlockEntity abovePile
                    && !abovePile.isEmpty())
                    return abovePile.grainType.copyWithCount(Math.min(amount, abovePile.totalCount));
                return ItemStack.EMPTY;
            }
            ItemStack extracted = extractGrain(amount);
            if (!extracted.isEmpty()) syncBlockState();
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {return getMaxCapacity();}

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            return slot == 0
                && stack.is(TFCThingsTags.Items.GRAIN_PILE_ITEMS)
                && (grainType.isEmpty() || ItemStack.isSameItem(grainType, stack));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        if (!grainType.isEmpty())
            tag.put(NBT_GRAIN, grainType.save(registries));
        tag.putBoolean(NBT_SHELTERED, sheltered);
        if (!cohorts.isEmpty())
        {
            ListTag list = new ListTag();
            for (Cohort c : cohorts)
            {
                CompoundTag ct = new CompoundTag();
                ct.putLong(NBT_COHORT_DATE, c.creationDate());
                ct.putInt(NBT_COHORT_COUNT, c.count());
                list.add(ct);
            }
            tag.put(NBT_COHORTS, list);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        grainType = tag.contains(NBT_GRAIN)
            ? ItemStack.parseOptional(registries, tag.getCompound(NBT_GRAIN))
            : ItemStack.EMPTY;

        sheltered = tag.getBoolean(NBT_SHELTERED);
        cohorts.clear();

        if (tag.contains(NBT_COHORTS, Tag.TAG_LIST))
        {
            ListTag list = tag.getList(NBT_COHORTS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++)
            {
                CompoundTag ct = list.getCompound(i);
                cohorts.add(new Cohort(ct.getLong(NBT_COHORT_DATE), ct.getInt(NBT_COHORT_COUNT)));
            }
        }
        totalCount = 0;
        for (Cohort c : cohorts) totalCount += c.count();
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
