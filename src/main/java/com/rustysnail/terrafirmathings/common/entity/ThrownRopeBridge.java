package com.rustysnail.terrafirmathings.common.entity;

import java.util.ArrayList;
import java.util.List;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlocks;
import com.rustysnail.terrafirmathings.common.TFCThingsEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.block.RopeBridgeBlock;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownRopeBridge extends ThrowableProjectile implements ItemSupplier
{

    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK =
        SynchedEntityData.defineId(ThrownRopeBridge.class, EntityDataSerializers.ITEM_STACK);

    public ThrownRopeBridge(EntityType<? extends ThrownRopeBridge> type, Level level)
    {
        super(type, level);
    }

    public ThrownRopeBridge(Level level, LivingEntity thrower, ItemStack stack)
    {
        super(TFCThingsEntities.THROWN_ROPE_BRIDGE.get(), thrower, level);
        this.setItem(stack);
    }

    @Override
    public ItemStack getItem()
    {
        ItemStack item = this.entityData.get(DATA_ITEM_STACK);
        return item.isEmpty() ? new ItemStack(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()) : item;
    }

    public void setItem(ItemStack stack)
    {
        this.entityData.set(DATA_ITEM_STACK, stack.copy());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        builder.define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        ItemStack item = this.getItem();
        if (!item.isEmpty())
        {
            tag.put("Item", item.save(this.registryAccess()));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Item"))
        {
            this.setItem(ItemStack.parse(this.registryAccess(), tag.getCompound("Item")).orElse(ItemStack.EMPTY));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        // Do nothing on entity hit, just pass through
    }

    @Override
    protected void onHitBlock(BlockHitResult result)
    {
        if (this.level().isClientSide())
        {
            this.discard();
            return;
        }

        BlockPos hitPos = result.getBlockPos();
        BlockState hitState = this.level().getBlockState(hitPos);


        if (hitState.getCollisionShape(this.level(), hitPos).isEmpty())
        {
            this.discard();
            return;
        }

        if (this.getOwner() instanceof Player player)
        {
            buildBridge(player, hitPos.above(), result.getLocation());
        }

        this.discard();
    }

    private void buildBridge(Player player, BlockPos end, Vec3 hitLocation)
    {
        BlockPos start = player.blockPosition();
        int maxLength = TFCThingsConfig.ITEMS.ROPE_BRIDGE.maxLength.get();
        int availableInInventory = countBridgeBundlesInInventory(player);

        int xDif = end.getX() - start.getX();
        int zDif = end.getZ() - start.getZ();
        boolean axis = Math.abs(zDif) > Math.abs(xDif);
        int length = axis ? Math.abs(zDif) : Math.abs(xDif);
        int yDif = start.getY() - end.getY();

        if (length - 1 > maxLength)
        {
            player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_exceeds_max", maxLength));
            return;
        }

        if (length - 1 > availableInInventory)
        {
            player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_too_long"));
            return;
        }

        if (length > 1 && ((length - 2) / 8) < Math.abs(yDif))
        {
            player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_too_steep"));
            return;
        }

        double secondaryDeviation = axis
            ? Math.abs(hitLocation.x - player.getX())
            : Math.abs(hitLocation.z - player.getZ());
        if (secondaryDeviation > 0.75)
        {
            player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_diagonal"));
            return;
        }

        Direction direction;
        if (axis)
        {
            direction = zDif > 0 ? Direction.SOUTH : Direction.NORTH;
        }
        else
        {
            direction = xDif > 0 ? Direction.EAST : Direction.WEST;
        }

        start = start.relative(direction);
        end = end.relative(direction.getOpposite());

        length = axis ? Math.abs(end.getZ() - start.getZ()) + 1 : Math.abs(end.getX() - start.getX()) + 1;

        List<BridgeInfo> bridgePath = calculateBridgePath(player, start, end, direction, yDif, length);
        if (bridgePath == null)
        {
            return;
        }

        for (BridgeInfo info : bridgePath)
        {
            BlockState bridgeState = TFCThingsBlocks.ROPE_BRIDGE.get().defaultBlockState()
                .setValue(RopeBridgeBlock.AXIS, axis)
                .setValue(RopeBridgeBlock.OFFSET, info.height);
            this.level().setBlock(info.pos, bridgeState, 3);
        }

        if (!player.getAbilities().instabuild)
        {
            consumeBridgeBundlesFromInventory(player, bridgePath.size());
        }
    }

    private int countBridgeBundlesInInventory(Player player)
    {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()))
            {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void consumeBridgeBundlesFromInventory(Player player, int amount)
    {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()))
            {
                int toRemove = Math.min(remaining, stack.getCount());
                stack.shrink(toRemove);
                remaining -= toRemove;
            }
        }
    }

    @Nullable
    private List<BridgeInfo> calculateBridgePath(Player player, BlockPos start, BlockPos end,
                                                 Direction direction, int yDif, int length)
    {
        List<BridgeInfo> bridgePath = new ArrayList<>();

        int startHeight = 0;
        int endHeight = 0;
        int startDif = yDif > 0 ? yDif + 1 : 1;
        int endDif = yDif < 0 ? Math.abs(yDif) + 1 : 1;
        int remainingPieces = length;

        BlockPos currentStart = start;
        BlockPos currentEnd = end;

        while (remainingPieces > 0)
        {
            if (startDif == endDif)
            {
                if (shouldReplaceBlock(currentStart))
                {
                    bridgePath.add(new BridgeInfo(currentStart, startHeight));
                    currentStart = currentStart.relative(direction);
                    startHeight = updateHeight(startHeight, startDif, currentStart, remainingPieces, endHeight);
                    if (startHeight == -1)
                    {
                        currentStart = currentStart.below();
                        startHeight = 7;
                        startDif--;
                    }
                    remainingPieces--;
                }
                else
                {
                    player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_interrupted"));
                    return null;
                }

                if (remainingPieces > 0)
                {
                    if (shouldReplaceBlock(currentEnd))
                    {
                        if (remainingPieces == 1 && endHeight < 7)
                        {
                            endHeight++;
                        }
                        bridgePath.add(new BridgeInfo(currentEnd, endHeight));
                        currentEnd = currentEnd.relative(direction.getOpposite());
                        endHeight = updateHeight(endHeight, endDif, currentEnd, remainingPieces, startHeight);
                        if (endHeight == -1)
                        {
                            currentEnd = currentEnd.below();
                            endHeight = 7;
                            endDif--;
                        }
                        remainingPieces--;
                    }
                    else
                    {
                        player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_interrupted"));
                        return null;
                    }
                }
            }
            else if (startDif > endDif)
            {
                if (shouldReplaceBlock(currentStart))
                {
                    bridgePath.add(new BridgeInfo(currentStart, startHeight));
                    currentStart = currentStart.relative(direction);
                    if (startHeight == 0)
                    {
                        if (startDif > 0 && shouldReplaceBlock(currentStart.below()))
                        {
                            currentStart = currentStart.below();
                            startHeight = 7;
                            startDif--;
                        }
                        else if (((remainingPieces - 1) / 8) < startDif - 1)
                        {
                            player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_too_steep"));
                            return null;
                        }
                    }
                    else
                    {
                        startHeight--;
                    }
                    remainingPieces--;
                }
                else
                {
                    player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_interrupted"));
                    return null;
                }
            }
            else
            {
                if (shouldReplaceBlock(currentEnd))
                {
                    bridgePath.add(new BridgeInfo(currentEnd, endHeight));
                    currentEnd = currentEnd.relative(direction.getOpposite());
                    if (endHeight == 0)
                    {
                        if (endDif > 0 && shouldReplaceBlock(currentEnd.below()))
                        {
                            currentEnd = currentEnd.below();
                            endHeight = 7;
                            endDif--;
                        }
                        else if (((remainingPieces - 1) / 8) < endDif - 1)
                        {
                            player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_too_steep"));
                            return null;
                        }
                    }
                    else
                    {
                        endHeight--;
                    }
                    remainingPieces--;
                }
                else
                {
                    player.sendSystemMessage(Component.translatable("tfcthings.tooltip.rope_bridge_interrupted"));
                    return null;
                }
            }
        }

        return bridgePath;
    }

    private int updateHeight(int currentHeight, int heightDif, BlockPos nextPos, int remaining, int targetHeight)
    {
        if (currentHeight == 0)
        {
            if (heightDif > 0 && shouldReplaceBlock(nextPos.below()))
            {
                return -1;
            }
            else if (currentHeight < targetHeight)
            {
                if (targetHeight - currentHeight >= remaining / 2)
                {
                    return currentHeight + 1;
                }
            }
        }
        else
        {
            if (currentHeight < targetHeight)
            {
                if (targetHeight - currentHeight >= remaining / 2)
                {
                    return currentHeight + 1;
                }
            }
            else
            {
                return currentHeight - 1;
            }
        }
        return currentHeight;
    }

    private boolean shouldReplaceBlock(BlockPos pos)
    {
        BlockState state = this.level().getBlockState(pos);
        if (!state.getFluidState().isEmpty() && state.getFluidState().getType() != Fluids.EMPTY)
        {
            return false;
        }
        return state.canBeReplaced();
    }

    private record BridgeInfo(BlockPos pos, int height) {}
}
