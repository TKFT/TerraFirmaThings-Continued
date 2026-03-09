package com.rustysnail.terrafirmathings.common.blockentity;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.block.GrindstoneBlock;
import com.rustysnail.terrafirmathings.common.item.GrindstoneItem;
import com.rustysnail.terrafirmathings.common.item.WhetstoneItem;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotationSinkBlockEntity;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.SinkNode;

public class GrindstoneBlockEntity extends BlockEntity implements RotationSinkBlockEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, GrindstoneBlockEntity be)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get()) return;
        be.refreshConnectionFromNeighbors(state);
        if (be.grindstone.isEmpty()) return;
        if (be.tool.isEmpty()) return;
        if (!(be.grindstone.getItem() instanceof GrindstoneItem grindstoneItem)) return;

        var rotation = be.node.rotation();
        if (rotation == null) return;

        float speed = Mth.abs(rotation.speed());
        if (speed < TFCThingsConfig.ITEMS.WHETSTONE.grindstoneMinSpeed.get().floatValue()) return;

        float multiplier = TFCThingsConfig.ITEMS.WHETSTONE.grindstoneSpeedMultiplier.get().floatValue();
        be.progressTicks += speed * multiplier;
        if (be.progressTicks < Math.max(1, grindstoneItem.getTier().getTicksPerOperation())) return;
        be.progressTicks = 0;

        int maxCharges = grindstoneItem.getTier().getMaxToolCharges();
        int added = applySharpness(be.tool, grindstoneItem.getTier().getChargesPerOperation(), maxCharges);
        if (added <= 0) return;

        if (damageOne(be.tool))
        {
            be.tool = ItemStack.EMPTY;
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5f, 0.9f);
        }
        if (damageOne(be.grindstone))
        {
            be.grindstone = ItemStack.EMPTY;
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.6f, 0.7f);
        }

        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS,
            0.5f, 1.0f + level.random.nextFloat() * 0.2f);
        be.markDirtyAndSync();
    }

    private static int applySharpness(ItemStack target, int chargesToAdd, int maxCharges)
    {
        return WhetstoneItem.applySharpness(target, chargesToAdd, maxCharges);
    }

    private static boolean damageOne(ItemStack stack)
    {
        if (stack.isEmpty() || !stack.isDamageableItem())
        {
            return false;
        }

        int nextDamage = stack.getDamageValue() + 1;
        if (nextDamage >= stack.getMaxDamage())
        {
            stack.shrink(1);
            return true;
        }

        stack.setDamageValue(nextDamage);
        return false;
    }

    private static Direction preferredConnectionFor(BlockState state)
    {
        Direction facing = state.hasProperty(GrindstoneBlock.FACING)
            ? state.getValue(GrindstoneBlock.FACING)
            : Direction.NORTH;
        return facing.getCounterClockWise();
    }

    private final SinkNode node;
    private Direction connection;
    private ItemStack grindstone = ItemStack.EMPTY;
    private ItemStack tool = ItemStack.EMPTY;
    private float progressTicks = 0;

    public GrindstoneBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCThingsBlockEntities.GRINDSTONE.get(), pos, state);

        this.connection = preferredConnectionFor(state);

        this.node = new SinkNode(pos, this.connection)
        {
            @Override
            public String toString()
            {
                return "Grindstone[pos=%s]".formatted(pos());
            }
        };
    }

    public ItemStack getGrindstone()
    {
        return grindstone;
    }

    public void setGrindstone(ItemStack stack)
    {
        this.grindstone = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.progressTicks = 0;
        markDirtyAndSync();
    }

    public ItemStack getTool()
    {
        return tool;
    }

    public void setTool(ItemStack stack)
    {
        this.tool = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.progressTicks = 0;
        markDirtyAndSync();
    }

    public void syncBlockState()
    {
        if (level == null || level.isClientSide())
        {
            return;
        }

        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof GrindstoneBlock))
        {
            return;
        }

        BlockState updated = state
            .setValue(GrindstoneBlock.HAS_GRINDSTONE, !grindstone.isEmpty())
            .setValue(GrindstoneBlock.HAS_TOOL, !tool.isEmpty());

        if (updated != state)
        {
            level.setBlock(worldPosition, updated, 3);
        }
    }

    @Override
    public void onChunkUnloaded()
    {
        super.onChunkUnloaded();
        performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        refreshConnectionFromNeighbors(getBlockState());
        performNetworkAction(NetworkAction.ADD);
        syncBlockState();
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }

    public float getRawRotationAngle(float partialTick)
    {
        var rotation = node.rotation();
        if (rotation == null) return 0f;
        return rotation.direction().getAxisDirection().getStep() * rotation.angle(partialTick);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        grindstone = tag.contains("Grindstone")
            ? ItemStack.parseOptional(registries, tag.getCompound("Grindstone"))
            : ItemStack.EMPTY;
        tool = tag.contains("Tool")
            ? ItemStack.parseOptional(registries, tag.getCompound("Tool"))
            : ItemStack.EMPTY;
        progressTicks = tag.getFloat("Progress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        if (!grindstone.isEmpty())
        {
            tag.put("Grindstone", grindstone.save(registries));
        }
        if (!tool.isEmpty())
        {
            tag.put("Tool", tool.save(registries));
        }
        tag.putFloat("Progress", progressTicks);
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

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        performNetworkAction(NetworkAction.REMOVE);
    }

    private void markDirtyAndSync()
    {
        setChanged();
        syncBlockState();
        if (level != null && !level.isClientSide())
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void refreshConnectionFromNeighbors(BlockState state)
    {
        if (level == null || level.isClientSide())
        {
            return;
        }

        Direction preferred = preferredConnectionFor(state);
        Direction alternate = preferred.getOpposite();

        boolean preferredValid = canConnectOnSide(preferred);
        boolean alternateValid = canConnectOnSide(alternate);

        Direction desired = preferred;
        if (preferredValid != alternateValid)
        {
            desired = preferredValid ? preferred : alternate;
        }
        else if (preferredValid)
        {
            desired = connection == alternate ? alternate : preferred;
        }

        if (desired != connection)
        {
            node.connections().clear();
            node.connections().add(desired);
            connection = desired;

            if (node.network() != Node.NO_NETWORK)
            {
                performNetworkAction(NetworkAction.UPDATE);
            }
            else
            {
                performNetworkAction(NetworkAction.ADD);
            }
        }
    }

    private boolean canConnectOnSide(Direction side)
    {
        if (level == null) return false;

        Direction preferred = preferredConnectionFor(getBlockState());
        if (side != preferred && side != preferred.getOpposite()) return false;

        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(side));
        if (neighbor instanceof RotatingBlockEntity rotating)
        {
            return rotating.getRotationNode().connections().contains(side.getOpposite());
        }
        return false;
    }
}
