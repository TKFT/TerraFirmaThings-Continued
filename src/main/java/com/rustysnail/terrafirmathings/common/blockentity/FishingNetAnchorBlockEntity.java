package com.rustysnail.terrafirmathings.common.blockentity;

import java.util.List;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.items.ItemStackHandler;

public class FishingNetAnchorBlockEntity extends BlockEntity implements MenuProvider
{
    private static final String NBT_INV = "Inventory";
    private static final String NBT_LINKED = "Linked";
    private static final String NBT_OTHER = "Other";
    private static final String NBT_AXIS = "Axis";
    private static final String NBT_MIN = "Min";
    private static final String NBT_MAX = "Max";
    private static final String NBT_Y_TOP = "YTop";
    private static final String NBT_Y_BOTTOM = "YBottom";
    private static final String NBT_LINK_ID = "LinkId";

    private static final String NET_COOLDOWN = "tfcthings_net_cd";
    private static final int SCAN_INTERVAL_TICKS = 5;
    private static final int ENTITY_COOLDOWN_TICKS = 10;

    private static final GameProfile NET_PROFILE = new GameProfile(
        UUID.fromString("b3f6bfe8-16c3-4d7b-9c75-10b2a3b820b0"),
        "tfcthings_fishing_net"
    );

    @SuppressWarnings("unused")
    public static void serverTick(Level level, BlockPos pos, BlockState state, FishingNetAnchorBlockEntity be)
    {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableFishingNet.get()) return;
        if (!be.isLinked()) return;
        if (!be.isMaster()) return;

        final long now = serverLevel.getGameTime();
        if (now % SCAN_INTERVAL_TICKS != 0) return;

        if (be.otherAnchor == null || !serverLevel.isLoaded(be.otherAnchor)) return;

        be.scanAndCatch(serverLevel);
    }

    private final ItemStackHandler inventory = new ItemStackHandler(27);
    private boolean linked;
    @Nullable private BlockPos otherAnchor;
    private Direction.Axis axis = Direction.Axis.X;
    private int minCoord;
    private int maxCoord;
    private int yTop;
    private int yBottom;
    private long linkId;

    public FishingNetAnchorBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.FISHING_NET_ANCHOR.get(), pos, state);
    }

    public void setLink(BlockPos otherAnchor, Direction.Axis axis, int minCoord, int maxCoord, int yTop, int yBottom, long linkId)
    {
        this.linked = true;
        this.otherAnchor = otherAnchor;
        this.axis = axis;
        this.minCoord = minCoord;
        this.maxCoord = maxCoord;
        this.yTop = yTop;
        this.yBottom = yBottom;
        this.linkId = linkId;
        setChanged();
    }

    public void clearLink()
    {
        this.linked = false;
        this.otherAnchor = null;
        this.linkId = 0;
        setChanged();
    }

    public boolean isLinked()
    {
        return linked && otherAnchor != null;
    }

    @Nullable
    public BlockPos getOtherAnchor()
    {
        return otherAnchor;
    }

    public boolean isMaster()
    {
        if (isLinked() && otherAnchor != null) return worldPosition.asLong() <= otherAnchor.asLong();
        return true;
    }

    public ItemStackHandler getInventory()
    {
        return inventory;
    }

    public BlockPos getMasterPos()
    {
        if (isLinked() && otherAnchor != null) return worldPosition.asLong() <= otherAnchor.asLong() ? worldPosition : otherAnchor;
        return worldPosition;
    }

    @Nullable
    public FishingNetAnchorBlockEntity getMaster(Level level)
    {
        final BlockPos mp = getMasterPos();
        if (!level.isLoaded(mp)) return null;
        final BlockEntity be = level.getBlockEntity(mp);
        return be instanceof FishingNetAnchorBlockEntity a ? a : null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean captureFishAndStore(ServerLevel serverLevel, LivingEntity fish)
    {
        if (fish.isRemoved() || !fish.isAlive()) return false;

        final FakePlayer player = FakePlayerFactory.get(serverLevel, NET_PROFILE);
        player.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);

        final var lootTableId = fish.getType().getDefaultLootTable();
        final LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableId);

        final DamageSource src = serverLevel.damageSources().playerAttack(player);

        final LootParams params = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, fish.position())
            .withParameter(LootContextParams.THIS_ENTITY, fish)
            .withParameter(LootContextParams.DAMAGE_SOURCE, src)
            .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
            .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, player)
            .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player)
            .create(LootContextParamSets.ENTITY);

        final List<ItemStack> drops = lootTable.getRandomItems(params);

        if (drops.isEmpty()) return false;

        fish.discard();

        boolean any = false;
        for (ItemStack stack : drops)
        {
            if (stack.isEmpty()) continue;
            any = true;

            ItemStack remaining = insertIntoMaster(stack);
            if (!remaining.isEmpty())
            {
                Containers.dropItemStack(serverLevel,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                    remaining);
            }
        }

        if (any) setChanged();
        return any;
    }

    public void dropAllItems(Level level, BlockPos pos)
    {
        FishingNetAnchorBlockEntity master = getMaster(level);
        if (master != null && master != this)
        {
            master.dropAllItems(level, pos);
            return;
        }

        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container.tfcthings.fishing_net");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player)
    {

        FishingNetAnchorBlockEntity master = this;
        if (level != null)
        {
            FishingNetAnchorBlockEntity resolved = getMaster(level);
            if (resolved != null) master = resolved;
        }

        return ChestMenu.threeRows(containerId, playerInv, new ProxyContainer(this, master));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        linked = tag.getBoolean(NBT_LINKED);
        if (tag.contains(NBT_OTHER)) otherAnchor = BlockPos.of(tag.getLong(NBT_OTHER));
        axis = "z".equalsIgnoreCase(tag.getString(NBT_AXIS)) ? Direction.Axis.Z : Direction.Axis.X;
        minCoord = tag.getInt(NBT_MIN);
        maxCoord = tag.getInt(NBT_MAX);
        yTop = tag.getInt(NBT_Y_TOP);
        yBottom = tag.getInt(NBT_Y_BOTTOM);
        linkId = tag.getLong(NBT_LINK_ID);

        if (tag.contains(NBT_INV))
        {
            inventory.deserializeNBT(registries, tag.getCompound(NBT_INV));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);

        tag.putBoolean(NBT_LINKED, linked);
        if (otherAnchor != null) tag.putLong(NBT_OTHER, otherAnchor.asLong());
        tag.putString(NBT_AXIS, axis.getName());
        tag.putInt(NBT_MIN, minCoord);
        tag.putInt(NBT_MAX, maxCoord);
        tag.putInt(NBT_Y_TOP, yTop);
        tag.putInt(NBT_Y_BOTTOM, yBottom);
        tag.putLong(NBT_LINK_ID, linkId);

        if (level == null || isMaster())
        {
            tag.put(NBT_INV, inventory.serializeNBT(registries));
        }
    }

    private void scanAndCatch(ServerLevel level)
    {
        if (!isLinked() || otherAnchor == null) return;

        final AABB curtain = getCurtain();

        final List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, curtain, e ->
            e.isAlive()
                && e.getType().is(TFCThingsTags.Entities.FISHING_NET_CATCHABLE)
                && level.getFluidState(e.blockPosition()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE)
        );

        if (targets.isEmpty()) return;

        for (LivingEntity living : targets)
        {
            final CompoundTag tag = living.getPersistentData();
            final long now = level.getGameTime();
            final long next = tag.getLong(NET_COOLDOWN);
            if (now < next) continue;
            tag.putLong(NET_COOLDOWN, now + ENTITY_COOLDOWN_TICKS);

            captureFishAndStore(level, living);
        }
    }

    private AABB getCurtain()
    {
        final int y0 = Math.min(yBottom, yTop);
        final int y1 = Math.max(yBottom, yTop) + 1;

        final int fixedX = worldPosition.getX();
        final int fixedZ = worldPosition.getZ();

        final AABB curtain;
        if (axis == Direction.Axis.X)
        {
            final int minX = Math.min(minCoord, maxCoord);
            final int maxX = Math.max(minCoord, maxCoord) + 1;
            curtain = new AABB(minX, y0, fixedZ, maxX, y1, fixedZ + 1);
        }
        else
        {
            final int minZ = Math.min(minCoord, maxCoord);
            final int maxZ = Math.max(minCoord, maxCoord) + 1;
            curtain = new AABB(fixedX, y0, minZ, fixedX + 1, y1, maxZ);
        }
        return curtain;
    }

    private ItemStack insertIntoMaster(ItemStack stack)
    {
        if (level == null) return stack;
        FishingNetAnchorBlockEntity master = getMaster(level);
        if (master == null) master = this;

        ItemStack remaining = stack;
        for (int i = 0; i < master.inventory.getSlots(); i++)
        {
            remaining = master.inventory.insertItem(i, remaining, false);
            if (remaining.isEmpty()) break;
        }
        master.setChanged();
        return remaining;
    }

    private record ProxyContainer(FishingNetAnchorBlockEntity clicked, FishingNetAnchorBlockEntity master) implements Container
    {
        @Override
        public int getContainerSize() {return master.inventory.getSlots();}

        @Override
        public boolean isEmpty()
        {
            for (int i = 0; i < master.inventory.getSlots(); i++)
            {
                if (!master.inventory.getStackInSlot(i).isEmpty()) return false;
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {return master.inventory.getStackInSlot(slot);}

        @Override
        public ItemStack removeItem(int slot, int amount)
        {
            ItemStack stack = master.inventory.getStackInSlot(slot);
            if (stack.isEmpty()) return ItemStack.EMPTY;

            ItemStack extracted = stack.split(amount);
            master.inventory.setStackInSlot(slot, stack);
            master.setChanged();
            return extracted;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot)
        {
            ItemStack stack = master.inventory.getStackInSlot(slot);
            master.inventory.setStackInSlot(slot, ItemStack.EMPTY);
            master.setChanged();
            return stack;
        }

        @Override
        public void setItem(int slot, ItemStack stack)
        {
            master.inventory.setStackInSlot(slot, stack);
            master.setChanged();
        }

        @Override
        public void setChanged() {master.setChanged();}

        @Override
        public boolean stillValid(Player player)
        {
            if (clicked.level == null) return false;
            return player.distanceToSqr(clicked.worldPosition.getX() + 0.5, clicked.worldPosition.getY() + 0.5, clicked.worldPosition.getZ() + 0.5) <= 64.0;
        }

        @Override
        public void clearContent()
        {
            for (int i = 0; i < master.inventory.getSlots(); i++)
            {
                master.inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
            master.setChanged();
        }
    }
}
